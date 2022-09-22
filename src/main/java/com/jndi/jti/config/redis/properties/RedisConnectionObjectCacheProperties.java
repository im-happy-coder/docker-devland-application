package com.jndi.jti.config.redis.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
@ConfigurationProperties(prefix = "spring.redis.object")
public class RedisConnectionObjectCacheProperties extends RedisProperties {
}
