package com.jndi.jti.board.mapper.oracle;

import com.jndi.jti.board.param.oracle.TxOracleParam;

import java.util.List;

public interface TxOracleMapper {
    public TxOracleParam oracleRead(Integer useid);
    public void oracleUpdate(TxOracleParam txOracleParam);
}
