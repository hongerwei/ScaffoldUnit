package org.crazycake.ScaffoldUnit.dao;

import org.crazycake.ScaffoldUnit.enums.DaoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaffoldUnitDaoFactory {
    
    private static Logger logger = LoggerFactory.getLogger(ScaffoldUnitDaoFactory.class);
    
    public static IScaffoldUnitDao getDao(String type){
        IScaffoldUnitDao dao = null;
        DaoType daoType = DaoType.valueOf(type);
        try{
            if(daoType ==  DaoType.mysql){
                dao = MysqlDao.getInstance();
            }else if(daoType == DaoType.hbase){
                dao = HbaseDao.getInstance();
            }
        }catch(Exception e){
            logger.error("get IScaffoldUnitDao error!",e);
        }
        return dao;
    }
}
