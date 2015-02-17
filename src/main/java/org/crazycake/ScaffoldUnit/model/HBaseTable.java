package org.crazycake.ScaffoldUnit.model;

public class HBaseTable {
    String tableName;
    String[] columnFamily;
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String[] getColumnFamily() {
        return columnFamily;
    }
    public void setColumnFamily(String[] columnFamily) {
        this.columnFamily = columnFamily;
    }
   
}
