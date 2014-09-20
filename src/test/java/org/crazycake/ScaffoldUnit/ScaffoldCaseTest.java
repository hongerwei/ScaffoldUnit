package org.crazycake.ScaffoldUnit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScaffoldCaseTest {

	public ScaffoldCaseTest(){
		System.out.println(111);
	}
	
	@BeforeClass
	public static void init(){
	}
	
	@Before
	public void setUp(){
		
	}
	
	@Test
	public void testComeAndBiteMe() throws IOException, SQLException {
		ScaffoldUnit.comeAndBiteMe();
	}
	
	@Test
	public void testBuild() throws IOException, SQLException {
		ScaffoldUnit.build();
	}
}
