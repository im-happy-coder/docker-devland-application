package com.jndi.jti.board.controller;

import com.jndi.jti.board.param.mysql.TxMysqlParam;
import com.jndi.jti.board.param.oracle.TxOracleParam;
import com.jndi.jti.board.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/", method = { RequestMethod.GET })
    public String transactionList(Integer bid, Integer useid, Model model, HttpSession session, InetAddress inetAddress) throws UnknownHostException {
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("sessionisNew", session.isNew());
        model.addAttribute("sessionCreateTime", new Date(session.getCreationTime()));
        model.addAttribute("sessionLastTime", new Date(session.getLastAccessedTime()));
        model.addAttribute("inetAddress", inetAddress.getLocalHost());
        model.addAttribute("mysqlRead", transactionService.mysqlRead(bid));
        model.addAttribute("oracleRead", transactionService.oracleRead(useid));
        return "jndi/txList";
    }

    @RequestMapping(value="/txUpdate", method = { RequestMethod.PUT })
    public String txUpdate(TxOracleParam txOracleParam, TxMysqlParam txMysqlParam){
        transactionService.txTransaction(txOracleParam, txMysqlParam);
        return "redirect:/";
    }
}
