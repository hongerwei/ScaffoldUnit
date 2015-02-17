package org.crazycake.ScaffoldUnit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.crazycake.ScaffoldUnit.utils.PropLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScaffoldUnitTest {
	
	@Test
	public void testComeAndBiteMe() throws Exception {
	    if(!"mysql".equals(PropLoader.getType())){
            return;
        }
		ScaffoldUnit.comeAndBiteMe();
	}
	
	@Test
	public void testMysqlBuild() throws Exception {
	    if(!"mysql".equals(PropLoader.getType())){
            return;
        }
		ScaffoldUnit.build();
	}
}
