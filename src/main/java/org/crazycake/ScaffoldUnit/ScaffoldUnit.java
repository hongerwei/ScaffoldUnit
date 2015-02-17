package org.crazycake.ScaffoldUnit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.crazycake.ScaffoldUnit.dao.IScaffoldUnitDao;
import org.crazycake.ScaffoldUnit.dao.ScaffoldUnitDaoFactory;
import org.crazycake.ScaffoldUnit.model.SCol;
import org.crazycake.ScaffoldUnit.model.SMethod;
import org.crazycake.ScaffoldUnit.model.STable;
import org.crazycake.ScaffoldUnit.model.Sconf;
import org.crazycake.ScaffoldUnit.utils.PropLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaffoldUnit {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static Logger logger = LoggerFactory.getLogger(ScaffoldUnit.class);
	
	private static IScaffoldUnitDao dao = ScaffoldUnitDaoFactory.getDao(PropLoader.getType());
	
	
	/**
	 * main build method
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void buildIt() throws Exception{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement s = stackTraceElements[3];
		
		String callerClassFullName = s.getClassName();
		String callerClassName = callerClassFullName.substring(callerClassFullName.lastIndexOf(".")+1, callerClassFullName.length());
		
		Sconf sconf = null;
		try {
			 sconf = mapper.readValue(Class.forName(callerClassFullName).getResourceAsStream(callerClassName+".json"), Sconf.class);
		} catch (Throwable e) {
		    logger.error("build scaffold error!",e);
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
		
		dao.cleanTables(ts);
		
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
	public static void build() throws Exception{
		buildIt();
	}
	
	/**
	 * assertThat (db version)!
	 * @param sql
	 * @param matcher
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static Object queryOneValue(String col, String tableName, SCol queryCondition) throws Exception{
		return dao.queryOneValue(col,tableName,queryCondition);
	}

	/**
	 * insert to all tables
	 * @param ts
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static void initialTables(List<STable> ts) throws Exception {
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
	private static void insertRows(STable t, List<List<SCol>> rs) throws Exception {
		for(int i =0 ;i<rs.size();i++){
			List<SCol> cs = rs.get(i);
			
			if(cs==null || cs.size()==0){
				continue;
			}
			
			dao.insertRow(t.getT(),cs);
		}
	}
	
	
	public static void wtf() throws Exception{
	    logger.info("ScaffoldUnit said: \"wtf!\"");
		buildIt();
	}
	
	public static void iHateWorkOvertime() throws Exception{
	    logger.info("ScaffoldUnit said: \"I hate work overtime!\"");
		buildIt();
	}
	
	public static void comeAndBiteMe() throws Exception{
	    logger.info("ScaffoldUnit said: \"Come and bite me!\"");
		buildIt();
	}
	
	public static void screwU() throws Exception{
	    logger.info("ScaffoldUnit said: \"Screw U!\"");
		buildIt();
	}
	
	public static void myBossIsAMuggle() throws Exception{
	    logger.info("ScaffoldUnit said: \"My boss is a muggle!\"");
		buildIt();
	}
}
