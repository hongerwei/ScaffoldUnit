package org.crazycake.ScaffoldUnit.utils;

import java.util.ArrayList;
import java.util.List;

import org.crazycake.ScaffoldUnit.ScaffoldUnit;
import org.crazycake.ScaffoldUnit.model.HBaseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseLoader {
    
    private static List<HBaseTable> tables;
    
    private static Logger logger = LoggerFactory.getLogger(HbaseLoader.class);
    
    static{
        tables = new ArrayList<HBaseTable>();
        List<String> hbaseInfo = FileTool.readLinedFile(ScaffoldUnit.class.getClassLoader().getResourceAsStream("ScaffoldUnit.hbase"));
        for(String info:hbaseInfo){
            String[] temp = info.split(":");
            HBaseTable hbaseTable = new HBaseTable();
            hbaseTable.setTableName(temp[0]);
            String[] families = temp[1].split(",");
            hbaseTable.setColumnFamily(families);
            tables.add(hbaseTable);
        }
    }

    public static List<HBaseTable> getTables() {
        return tables;
    }
    
}
