package org.crazycake.ScaffoldUnit;

import static org.hamcrest.CoreMatchers.is;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.crazycake.ScaffoldUnit.model.SCol;
import org.crazycake.ScaffoldUnit.utils.PropLoader;
import org.junit.Assert;
import org.junit.Test;

public class HelloScaffoldUnitTest {
	
	@Test
	public void testMysqlBuild() throws Exception{
		
	    if(!"mysql".equals(PropLoader.getType())){
	        return;
	    }
		//build the scaffold data
		ScaffoldUnit.build();
		
		//test your code
		InputStream inputStream = ScaffoldUnit.class.getClassLoader().getResourceAsStream("ScaffoldUnit.properties");
		Properties prop = new Properties();
		prop.load(inputStream);
		Connection conn = null;
		conn = DriverManager.getConnection(prop.getProperty("ScaffoldUnit.jdbc.url"),prop.getProperty("ScaffoldUnit.jdbc.username"), prop.getProperty("ScaffoldUnit.jdbc.password"));
		Statement stat = conn.createStatement();
		stat.execute("update student set name='ted' where name='jack'");
		stat.close();
		conn.close();
		
		//assert your result
		SCol queryCondition = new SCol();
		queryCondition.setC("id");
		queryCondition.setV(1);
		Object actual = ScaffoldUnit.queryOneValue("name","student",queryCondition);
		Assert.assertThat((String)actual,is("ted"));
	}
	
	@Test
    public void testHbaseBuild() throws Exception{
        
        if(!"hbase".equals(PropLoader.getType())){
            return;
        }
        //build the scaffold data
        ScaffoldUnit.build();
        
      //test your code
        //assert your result
        SCol queryCondition = new SCol();
        queryCondition.setC("info1:name");
        queryCondition.setV("ted");
        Object actual = ScaffoldUnit.queryOneValue("info1:name","student",queryCondition);
        Assert.assertThat((String)actual,is("ted"));
    }
}
