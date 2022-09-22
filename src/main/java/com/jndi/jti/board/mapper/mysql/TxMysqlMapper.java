package com.jndi.jti.board.mapper.mysql;

import com.jndi.jti.board.param.mysql.TxMysqlParam;

import java.util.List;

public interface TxMysqlMapper {
        public TxMysqlParam mysqlRead(Integer bid);
        public void mysqlUpdate(TxMysqlParam txMysqlParam);
}
