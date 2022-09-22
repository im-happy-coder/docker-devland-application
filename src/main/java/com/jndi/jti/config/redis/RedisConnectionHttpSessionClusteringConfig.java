package com.jndi.jti.config.redis;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jndi.jti.config.redis.properties.RedisConnectionHttpSessionCacheProperties;
import com.jndi.jti.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@ConditionalOnProperty(prefix = "spring.redis.session", name = "host")
@Configuration
@Slf4j
public class RedisConnectionHttpSessionClusteringConfig {

    @Autowired
    protected RedisConnectionHttpSessionCacheProperties redisConnectionHttpSessionCacheProperties;

    @Bean(name = "httpSessionConnectionFactory")
    public RedisConnectionFactory clusterConnectionFactory() {
        try {
        return new JedisConnectionFactory(
                    new RedisClusterConfiguration(redisConnectionHttpSessionCacheProperties.getCluster().getNodes()));
        } catch (NullPointerException ne) {
            return new JedisConnectionFactory();
        }
    }

    @Primary
    @Bean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }
}
