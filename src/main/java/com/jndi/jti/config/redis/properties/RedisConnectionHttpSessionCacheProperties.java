package com.jndi.jti.config.redis.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis.session")
public class RedisConnectionHttpSessionCacheProperties extends RedisProperties { }
