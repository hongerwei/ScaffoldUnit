package org.crazycake.ScaffoldUnit.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.crazycake.ScaffoldUnit.model.SCol;
import org.crazycake.ScaffoldUnit.model.STable;

/**
 * ScaffoldUnitDao
 * @author alexxiyang (https://github.com/alexxiyang)
 *
 */
public interface IScaffoldUnitDao {
    
    public void cleanTables(List<STable> ts) throws Exception;
    
    public void insertRow(String t, List<SCol> cs) throws Exception;
    
    public Object queryOneValue(String col, String tableName, SCol queryCondition) throws Exception;
}
