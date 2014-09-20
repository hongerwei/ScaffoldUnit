package org.crazycake.ScaffoldUnit;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.crazycake.ScaffoldUnit.dao.ScaffoldUnitDao;
import org.crazycake.ScaffoldUnit.model.SCol;
import org.crazycake.ScaffoldUnit.model.SMethod;
import org.crazycake.ScaffoldUnit.model.STable;
import org.crazycake.ScaffoldUnit.model.Sconf;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaffoldUnit {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static Logger logger = LoggerFactory.getLogger(ScaffoldUnit.class);
	
	private static ScaffoldUnitDao dao = new ScaffoldUnitDao();
	
	
	/**
	 * main build method
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void buildIt() throws IOException, SQLException{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement s = stackTraceElements[3];
		
		String callerClassFullName = s.getClassName();
		String callerClassName = callerClassFullName.substring(callerClassFullName.lastIndexOf(".")+1, callerClassFullName.length());
		
		Sconf sconf = null;
		try {
			 sconf = mapper.readValue(Class.forName(callerClassFullName).getResourceAsStream(callerClassName+".json"), Sconf.class);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if(sconf==null){
			return;
		}
		
		List<SMethod> ms = sconf.getMs();
		if(ms == null || ms.size()==0){
			return;
		}
		
		SMethod smethod = findMethod(s, ms);
		
		if(smethod == null){
			return;
		}
		
		List<STable> ts = smethod.getTs();
		if(ts==null || ts.size()==0){
			return;
		}
		
		cleanTables(ts);
		
		initialTables(ts);
	}
	
	/**
	 * find the matchd smethod
	 * @param s
	 * @param ms
	 * @return
	 */
	private static SMethod findMethod(StackTraceElement s, List<SMethod> ms) {
		String methodName = s.getMethodName();
		SMethod smethod = null;
		for(SMethod m:ms){
			if(methodName.equals(m.getN())){
				smethod = m;
				break;
			}
		}
		return smethod;
	}
	
	/**
	 * build your scaffold
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void build() throws IOException, SQLException{
		buildIt();
	}
	
	/**
	 * assertThat (db version)!
	 * @param sql
	 * @param matcher
	 * @throws SQLException
	 */
	public static void dbAssertThat(String sql,Matcher matcher) throws SQLException{
		Object actual = dao.queryOneValue(sql);
		assertThat(sql, actual, matcher);
	}

	/**
	 * insert to all tables
	 * @param ts
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void initialTables(List<STable> ts) throws IOException, SQLException {
		//insert
		for(STable t:ts){
			
			List<List<SCol>> rs = t.getRs();
			if(rs==null || rs.size()==0){
				continue;
			}
			
			insertRows(t, rs);
		}
	}
	
	/**
	 * insert mutiple rows
	 * @param t
	 * @param rs
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void insertRows(STable t, List<List<SCol>> rs) throws IOException, SQLException {
		for(int i =0 ;i<rs.size();i++){
			List<SCol> cs = rs.get(i);
			
			if(cs==null || cs.size()==0){
				continue;
			}
			
			insertRow(t.getT(),cs);
		}
	}
	
	/**
	 * insert one row
	 * @param t
	 * @param cs
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void insertRow(String t, List<SCol> cs) throws IOException, SQLException{
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
		dao.execute(insertSql);
	}

	/**
	 * truncate all tables
	 * @param ts
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void cleanTables(List<STable> ts) throws IOException, SQLException {
		//clean
		for(STable t:ts){
			dao.execute("truncate table " + t.getT());
		}
	}
	
	public static void wtf() throws IOException, SQLException{
		System.out.println("ScaffoldUnit said: \"wtf!\"");
		buildIt();
	}
	
	public static void iHateWorkOvertime() throws IOException, SQLException{
		System.out.println("ScaffoldUnit said: \"I hate work overtime!\"");
		buildIt();
	}
	
	public static void comeAndBiteMe() throws IOException, SQLException{
		System.out.println("ScaffoldUnit said: \"Come and bite me!\"");
		buildIt();
	}
	
	public static void screwU() throws IOException, SQLException{
		System.out.println("ScaffoldUnit said: \"Screw U!\"");
		buildIt();
	}
	
	public static void myBossIsAMuggle() throws IOException, SQLException{
		System.out.println("ScaffoldUnit said: \"My boss is a muggle!\"");
		buildIt();
	}
}
