package com.jndi.jti.config.datasource;

import javax.sql.DataSource;

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
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

@MapperScan(basePackages = "com.jndi.jti.**.mapper.mysql", sqlSessionTemplateRef = "sqlSessionTemplate4JndiMysql")
@Configuration
public class MySQLDataSourceConfiguration {


	@Autowired
	private ConfigurableEnvironment environment;

	@Value("${spring.mysql.datasource.jndi}")
	private boolean isJndiDataSource;

	@Bean(name = "dataSource4BaseJndiMysql", destroyMethod = "close")
	@ConfigurationProperties(prefix = "ttaeinee.datasource.mysql")
	public DataSource dataSource4BaseJndiMysql() {
		if (isJndiDataSource) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource("jdbc/MYSQL_DB");
		} else {
			return DataSourceBuilder.create().build();
		}
	}

	//java:/comp/env/
	@Primary
	@Bean(name = "dataSource4JndiMysql")
	public DataSource dataSource4JndiMysql(@Qualifier("dataSource4BaseJndiMysql") DataSource dataSource) {
		return new LazyConnectionDataSourceProxy(dataSource);
	}

	@Bean(name = "sqlSessionFactory4JndiMysql")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource4JndiMysql") DataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setVfs(SpringBootVFS.class);

		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

		sqlSessionFactoryBean.setConfigLocation(
				resourcePatternResolver
						.getResource(environment.getProperty("ttaeinee.datasource.mysql.config-location")));

		sqlSessionFactoryBean.setMapperLocations(
				resourcePatternResolver
						.getResources(environment.getProperty("ttaeinee.datasource.mysql.mapper-locations")));

		return sqlSessionFactoryBean.getObject();
	}

	@Bean(name = "sqlSessionTemplate4JndiMysql", destroyMethod = "clearCache")
	public SqlSessionTemplate sqlSessionTemplate(
			@Qualifier("sqlSessionFactory4JndiMysql") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Primary
	@Bean(name = "txManager4Mysql")
	public DataSourceTransactionManager transactionManager(
			@Qualifier("dataSource4JndiMysql") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
