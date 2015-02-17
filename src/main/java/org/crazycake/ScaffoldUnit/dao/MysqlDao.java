package org.crazycake.ScaffoldUnit.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.crazycake.ScaffoldUnit.ScaffoldUnit;
import org.crazycake.ScaffoldUnit.model.SCol;
import org.crazycake.ScaffoldUnit.model.STable;
import org.crazycake.ScaffoldUnit.utils.PropLoader;
import org.crazycake.ScaffoldUnit.utils.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysqlDao implements IScaffoldUnitDao{

    private static boolean initialized = false;
    
    private static Connection conn;
    
    private static Logger logger = LoggerFactory.getLogger(MysqlDao.class);
    
    private static MysqlDao _self = null;
    
    private MysqlDao() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(PropLoader.getUrl(),PropLoader.getUsername(), PropLoader.getPassword());
    }
    
    public static MysqlDao getInstance() throws ClassNotFoundException, SQLException{
        if(_self == null){
            _self = new MysqlDao();
        }
        return _self;
    }
    
    /**
     * Execute sql. If database structure didn't been built, it will use ScaffoldUnit.sql to create it!
     * @param sql
     * @throws IOException
     * @throws SQLException
     */
    private void execute(String sql) throws IOException, SQLException{
        Statement stat  = null;
        try {
            stat = conn.createStatement();
            logger.debug(sql);
            stat.execute(sql);
        } catch (SQLException e) {
            logger.error("ScaffoldUnit execute SQL error! SQL: "+sql,e);
            String errorMessage = e.getMessage();
            if(errorMessage.startsWith("Table ") && errorMessage.endsWith(" doesn't exist") && !initialized){
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
    public Object queryOneValue(String col, String tableName, SCol queryCondition) throws SQLException{
        
        String conditionValue = null;
        if(queryCondition.getV() instanceof String){
            conditionValue = "'" + queryCondition.getV() + "'";
        }else{
            conditionValue = queryCondition.getV().toString();
        }
        String sql = "select " + col + " from " + tableName + " where " + queryCondition.getC() + " = " + conditionValue;
        
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
    
    protected static boolean initStructure() throws IOException, SQLException{
        initialized = true;
        
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
    
    /**
     * truncate all tables
     * @param ts
     * @throws SQLException 
     * @throws IOException 
     */
    public void cleanTables(List<STable> ts) throws IOException, SQLException {
        //clean
        for(STable t:ts){
            execute("truncate table " + t.getT());
        }
    }
    
    /**
     * insert one row
     * @param t
     * @param cs
     * @throws SQLException 
     * @throws IOException 
     */
    public void insertRow(String t, List<SCol> cs) throws IOException, SQLException{
        StringBuilder fields = new StringBuilder();
        fields.append("(");
        
        StringBuilder values = new StringBuilder();
        values.append("(");
        
        for(int j=0;j<cs.size();j++){
            
            SCol c = cs.get(j);
            
            if(j!=0){
                fields.append(",");
                values.append(",");
            }
            fields.append(c.getC());
            values.append(c.getV());
            
            if(j==cs.size()-1){
                fields.append(")");
                values.append(")");
            }
        }
        
        String insertSql = "insert into " + t + " " +  fields.toString() + " values " + values.toString();
        execute(insertSql);
    }
}
