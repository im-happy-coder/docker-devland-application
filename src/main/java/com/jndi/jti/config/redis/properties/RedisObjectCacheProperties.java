package com.jndi.jti.config.redis.properties;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "db.cache")
@Data
public class RedisObjectCacheProperties {
    private Long defaultExpireTime;
    private Map<String, Long> expires;
}
