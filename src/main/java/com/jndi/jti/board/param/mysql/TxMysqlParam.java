package com.jndi.jti.board.param.mysql;

import lombok.Data;

@Data
public class TxMysqlParam {
    private String bid;
    private String title;
    private String content;
    private String writer;
    private String passwd;
    private String writeDate;
}
