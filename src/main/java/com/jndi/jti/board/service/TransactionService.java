package com.jndi.jti.board.service;

import com.jndi.jti.board.mapper.mysql.TxMysqlMapper;
import com.jndi.jti.board.mapper.oracle.TxOracleMapper;
import com.jndi.jti.board.param.mysql.TxMysqlParam;
import com.jndi.jti.board.param.oracle.TxOracleParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "cacheTx")
public class TransactionService {

    @Autowired
    private TxMysqlMapper txMysqlMapper;

    @Autowired
    private TxOracleMapper txOracleMapper;

//    @CacheEvict(cacheNames = "mList", allEntries = true)
    public TxMysqlParam mysqlRead(Integer bid){
        return txMysqlMapper.mysqlRead(bid);
    }

//    @CacheEvict(cacheNames = "oList", allEntries = true)
    public TxOracleParam oracleRead(Integer useid){
        return txOracleMapper.oracleRead(useid);
    }

//    @Cacheable(cacheManager = "cacheManager" , cacheNames = "CacheUpdateTx", key = "#txOracleParam.toString()")
    @Transactional("txManagerMyOr")
    public void txTransaction(TxOracleParam txOracleParam, TxMysqlParam txMysqlParam){
        txOracleMapper.oracleUpdate(txOracleParam);
        txMysqlMapper.mysqlUpdate(txMysqlParam);
    }

}
