package com.jndi.jti.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transaction;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ChainedTransactionManagerConfig {

    @Bean(name = "txManagerMyOr")
    public PlatformTransactionManager annotationDriverManagerMyOr(
            @Qualifier("txManager4Oracle") DataSourceTransactionManager txManager4Oracle,
            @Qualifier("txManager4Mysql") DataSourceTransactionManager txManager4Mysql) {

        PlatformTransactionManager chainedTransactionManager = new ChainedTransactionManager(txManager4Oracle, txManager4Mysql);

        TransactionTemplate transactionTemplate = new TransactionTemplate(chainedTransactionManager);
        transactionTemplate.setTimeout(30);

        PlatformTransactionManager platformTransactionManager = transactionTemplate.getTransactionManager();

        return platformTransactionManager;
    }
}
