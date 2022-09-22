package com.jndi.jti.config.datasource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

@MapperScan(basePackages = "com.jndi.jti.**.mapper.oracle", sqlSessionTemplateRef = "sqlSessionTemplate4JndiOracle")
@Configuration
public class OracleDataSourceConfiguration {

    @Autowired
    private ConfigurableEnvironment environment;

    @Value("${spring.oracle.datasource.jndi}")
    private boolean isJndiDataSource;

    @Bean(name = "dataSource4BaseJndiOracle", destroyMethod = "close")
    @ConfigurationProperties(prefix = "ttaeinee.datasource.oracle")
    public DataSource dataSource4BaseJndiOracle() {
        if(isJndiDataSource) {
            JndiDataSourceLookup lookup = new JndiDataSourceLookup();
            return lookup.getDataSource("jdbc/ORACLE_DB");
        } else {
            return DataSourceBuilder.create().build();
        }
    }

    @Bean(name = "dataSource4JndiOracle")
    public DataSource dataSource4JndiOracle(@Qualifier("dataSource4BaseJndiOracle")DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }

    @Bean(name = "sqlSessionFactory4JndiOracle")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource4JndiOracle") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        sqlSessionFactoryBean.setConfigLocation(
                resourcePatternResolver
                        .getResource(environment.getProperty("ttaeinee.datasource.oracle.config-location")));

        sqlSessionFactoryBean.setMapperLocations(
                resourcePatternResolver
                        .getResources(environment.getProperty("ttaeinee.datasource.oracle.mapper-locations")));

        return sqlSessionFactoryBean.getObject();

    }

    @Bean(name = "sqlSessionTemplate4JndiOracle", destroyMethod = "clearCache")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory4JndiOracle") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "txManager4Oracle")
    public DataSourceTransactionManager transactionManager(
            @Qualifier("dataSource4JndiOracle") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
