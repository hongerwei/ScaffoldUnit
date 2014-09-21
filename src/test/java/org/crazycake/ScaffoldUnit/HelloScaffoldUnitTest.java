package org.crazycake.ScaffoldUnit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class HelloScaffoldUnitTest {
	
	@Test
	public void testBuild() throws IOException, SQLException, ClassNotFoundException{
		
		//build the scaffold data
		ScaffoldUnit.build();
		
		//test your code
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sunit_test?useUnicode=true&characterEncoding=UTF-8","root", "qwer1234");
		Statement stat = conn.createStatement();
		stat.execute("update student set name='ted' where name='jack'");
		stat.close();
		conn.close();
		
		//assert your result
		ScaffoldUnit.dbAssertThat("select name from student where id=1", is("ted"));
		
	}
}
