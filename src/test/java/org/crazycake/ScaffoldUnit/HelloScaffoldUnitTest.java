package org.crazycake.ScaffoldUnit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class HelloScaffoldUnitTest {
	
	@Test
	public void testBuild() throws IOException, SQLException, ClassNotFoundException{
		
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
		ScaffoldUnit.dbAssertThat("select name from student where id=1", is("ted"));
		
	}
}
