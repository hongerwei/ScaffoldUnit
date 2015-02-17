package org.crazycake.ScaffoldUnit.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.crazycake.ScaffoldUnit.model.HBaseTable;
import org.crazycake.ScaffoldUnit.model.SCol;
import org.crazycake.ScaffoldUnit.model.STable;
import org.crazycake.ScaffoldUnit.utils.HbaseLoader;
import org.crazycake.ScaffoldUnit.utils.PropLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseDao implements IScaffoldUnitDao {
    
    private static HBaseAdmin admin;
    
    private static Configuration config;
    
    private static Logger logger = LoggerFactory.getLogger(HbaseDao.class);
    
    private static HbaseDao _self = null;
    
    private HbaseDao() throws Exception{
        config = HBaseConfiguration.create(); 
        
        String zookeeperUrl = PropLoader.getUrl();
        String[] znodes = zookeeperUrl.split(",");
        StringBuilder quorum = new StringBuilder();
        String zookeeperPort = "";
        for(int i=0;i<znodes.length;i++){
            if(i!=0){
                quorum.append(",");
            }
            quorum.append(znodes[i].split(":")[0]);
            zookeeperPort = znodes[i].split(":")[1];
        }
        
        config.set("hbase.zookeeper.quorum", quorum.toString());  
        config.set("hbase.zookeeper.property.clientPort", zookeeperPort);  
        config.set("hbase.cluster.distributed", "true");
        
        admin = new HBaseAdmin(config);
        
        //initial database
        List<HBaseTable> hbaseTables = HbaseLoader.getTables();
        for(HBaseTable table:hbaseTables){
            byte[] tableName = table.getTableName().getBytes();
            if(admin.isTableAvailable(tableName)){
                if(admin.isTableEnabled(tableName)){
                    admin.disableTable(tableName);
                }
                
                admin.deleteTable(tableName);
            }
            
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(table.getTableName()));  
            
            for(String colFamily:table.getColumnFamily()){
                tableDescriptor.addFamily(new HColumnDescriptor(colFamily));
            }
              
            admin.createTable(tableDescriptor);
            logger.debug("create table : " + table.getTableName() + " [done]");
        }
        
    }
    
    public static HbaseDao getInstance() throws Exception{
        if(_self == null){
            _self = new HbaseDao();
        }
        return _self;
    }
    
    /**
     * Query one value of first row of result of this sql. If there is no result, it return null.
     * @param sql
     * @return
     * @throws SQLException
     */
    public Object queryOneValue(String col, String tableName, SCol queryCondition) throws Exception{
        
        String actual = null;
        
        HTable table = new HTable(config, tableName);
        
        String queryCol = queryCondition.getC();
        String[] temp  = queryCol.split(":");
        byte[] queryColFamily = Bytes.toBytes(temp[0]);
        byte[] queryColName = Bytes.toBytes(temp[1]);
        
        RegexStringComparator queryComp = new RegexStringComparator(queryCondition.getV().toString());
        
        SingleColumnValueFilter filter = new SingleColumnValueFilter(queryColFamily, queryColName, CompareOp.EQUAL, queryComp);  
  
        Scan scan = new Scan();
        
        scan.setFilter(filter);
        
        ResultScanner results = table.getScanner(scan); 
        
        
        String[] resultCol = col.split(":");
        byte[] resultColFamily = Bytes.toBytes(resultCol[0]);
        byte[] resultColName = Bytes.toBytes(resultCol[1]);
        byte[] resultByte = null;
        int i=0;
        for (Result result : results) {
            if(i>0){
                break;
            }
            resultByte = result.getValue(resultColFamily, resultColName);  
            i++;
        }
        table.close();
        if(resultByte != null){
            actual = new String(resultByte);
        }
        
        return actual;
    }

    
    /**
     * truncate all tables
     * @param ts
     * @throws SQLException 
     * @throws IOException 
     */
    public void cleanTables(List<STable> ts) throws Exception {
        //clean
        for(STable t:ts){
            HTableDescriptor tableDescriptor = admin.getTableDescriptor(t.getT().getBytes());
            admin.disableTable(t.getT().getBytes());
            admin.deleteTable(t.getT().getBytes());
            admin.createTable(tableDescriptor);
            logger.debug("clean table : " + t.getT() + " [done]");
        }
    }
    
    /**
     * insert one row
     * @param t
     * @param cs
     * @throws SQLException 
     * @throws IOException 
     */
    public void insertRow(String t, List<SCol> cs) throws Exception{
        
        HTable table = new HTable(config, t);
        
        SCol firstCol = cs.get(0);
        
        // first col is rowkey
        Put row = new Put(Bytes.toBytes(firstCol.getV().toString()));  
        
        for (int i = 1; i< cs.size(); i++) {
            SCol currentCol = cs.get(i);
            String[] temp = currentCol.getC().split(":");
            byte[] colFamily = Bytes.toBytes(temp[0]);
            byte[] colName = Bytes.toBytes(temp[1]);
            row.add(colFamily, colName, Bytes.toBytes(currentCol.getV().toString()));  
            
        }
        table.put(row);  
        
        // 最后要记得提交和关闭表  
        table.flushCommits();  
        table.close();
    }

}
