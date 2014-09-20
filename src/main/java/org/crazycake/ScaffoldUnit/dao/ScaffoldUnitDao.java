package org.crazycake.ScaffoldUnit.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.crazycake.ScaffoldUnit.ScaffoldUnit;
import org.crazycake.ScaffoldUnit.utils.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ScaffoldUnitDao
 * @author alexxiyang (https://github.com/alexxiyang)
 *
 */
public class ScaffoldUnitDao {

	private static Connection conn;
	
	private static String url;
	private static String username;
	private static String password;
	
	private static Logger logger = LoggerFactory.getLogger(ScaffoldUnitDao.class);
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection(url,username, password);
		return conn;
	}
	
	static{
		Properties prop = new Properties();
		InputStream inputStream = ScaffoldUnit.class.getClassLoader().getResourceAsStream("ScaffoldUnit.properties");
		try {
			prop.load(inputStream);
			
			url = prop.getProperty("ScaffoldUnit.jdbc.url");
			logger.debug("ScaffoldUnit.jdbc.url="+url);
			
			username = prop.getProperty("ScaffoldUnit.jdbc.username");
			logger.debug("ScaffoldUnit.jdbc.username="+username);
			
			password = prop.getProperty("ScaffoldUnit.jdbc.password");
			logger.debug("ScaffoldUnit.jdbc.password="+password);
			
			//init Connection
			conn = getConnection();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Execute sql. If database structure didn't been built, it will use ScaffoldUnit.sql to create it!
	 * @param sql
	 * @throws IOException
	 * @throws SQLException
	 */
	public void execute(String sql) throws IOException, SQLException{
		Statement stat  = null;
		try {
			stat = conn.createStatement();
			logger.debug(sql);
			stat.execute(sql);
		} catch (SQLException e) {
			logger.error("ScaffoldUnit execute SQL error! SQL: "+sql,e);
			String errorMessage = e.getMessage();
			if(errorMessage.startsWith("Table ") && errorMessage.endsWith(" doesn't exist")){
				boolean initOk = initStructure();
				
				if(initOk){
					//try to execute again!
					execute(sql);
				}
			}else{
				throw e;
			}
		}finally{
			stat.close();
		}
	}
	
	/**
	 * Query one value of first row of result of this sql. If there is no result, it return null.
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public Object queryOneValue(String sql) throws SQLException{
		Object actual = null;
		Statement stat = null;
		try {
			stat = conn.createStatement();
			logger.debug(sql);
			ResultSet rs = stat.executeQuery(sql);
			if(rs.next()){
				actual = rs.getObject(1);
			}
		} catch (SQLException e) {
			logger.error("ScaffoldUnit execute SQL error! SQL: "+sql,e);
			throw e;
		} finally{
			stat.close();
		}
		return actual;
	}
	
	protected boolean initStructure() throws IOException, SQLException{
		logger.info("Use ScaffoldUnit.sql to initialize structure. Please make sure ScaffoldUnit.sql is at your classpath!");
		ScriptRunner runner = new ScriptRunner(conn, false, true);
		
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(ScaffoldUnit.class.getClassLoader().getResourceAsStream("ScaffoldUnit.sql"));
		} catch (Exception e) {
			logger.info("Can't find ScaffoldUnit.sql at your classpath. Can't initialize database structure!");
			return false;
		}
		runner.runScript(reader);
		logger.info("Use ScaffoldUnit.sql to initialize structure. [ok]");
		return true;
	}
}
