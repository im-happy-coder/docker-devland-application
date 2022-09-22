package com.jndi.jti.config.redis;

import com.jndi.jti.config.redis.properties.RedisConnectionObjectCacheProperties;
import com.jndi.jti.config.redis.properties.RedisObjectCacheProperties;
import com.jndi.jti.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "spring.redis.object", name = "host")
@Configuration
@EnableConfigurationProperties({ RedisObjectCacheProperties.class })
@Slf4j
public class RedisConnectionObjectCacheConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisConnectionObjectCacheProperties redisConnectionObjectCacheProperties;

    @Autowired
    private RedisObjectCacheProperties redisObjectCacheProperties;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Primary
    @Bean(name = "objectCacheConnectionFactory")
    public RedisConnectionFactory connectionFactory() {
        Pool pool = this.redisConnectionObjectCacheProperties.getPool();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName(this.redisConnectionObjectCacheProperties.getHost());
        jedisConnectionFactory.setPort(this.redisConnectionObjectCacheProperties.getPort());
        jedisConnectionFactory.setDatabase(this.redisConnectionObjectCacheProperties.getDatabase());
        return jedisConnectionFactory;
    }

    @Bean(name = "objectCacheWithDatabaseConnectionFactory")
    public RedisConnectionFactory withDatabaseConnectionFactory(){
        Pool pool = this.redisConnectionObjectCacheProperties.getPool();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setTestWhileIdle(true);

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName(this.redisConnectionObjectCacheProperties.getHost());
        jedisConnectionFactory.setPort(this.redisConnectionObjectCacheProperties.getPort());
        jedisConnectionFactory.setDatabase(this.redisConnectionObjectCacheProperties.getDatabase());
        return jedisConnectionFactory;
    }

    @Bean(name = "objectCacheRedisTemplate")
    public RedisTemplate<Serializable, Serializable> redisTemplate(
            @Qualifier("objectCacheConnectionFactory") RedisConnectionFactory factory){
        RedisTemplate<Serializable, Serializable> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(stringRedisSerializer);
        template.setStringSerializer(stringRedisSerializer);
        template.setEnableDefaultSerializer(true);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        return template;
    }
    @Bean(name = "objectCacheRedisWithDatabaseTemplate")
    public RedisTemplate<Serializable, Serializable> redisWithDatabaseTemplate(
            @Qualifier("objectCacheWithDatabaseConnectionFactory") RedisConnectionFactory factory){
        RedisTemplate<Serializable, Serializable> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(stringRedisSerializer);
        template.setStringSerializer(stringRedisSerializer);
        template.setEnableDefaultSerializer(true);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        return template;
    }

    @Bean
    public CacheManager cacheManager(
            @Qualifier("objectCacheRedisTemplate") RedisTemplate<Serializable, Serializable> redisTemplate){
        if (activeProfile.contains("prj")) {
            return new NoOpCacheManager();
        } else {
            RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);

            Long defaultExpireTime = redisObjectCacheProperties.getDefaultExpireTime();
            Map<String, Long> expires = redisObjectCacheProperties.getExpires();

            if (defaultExpireTime != null) {
                cacheManager.setDefaultExpiration(defaultExpireTime);
            }

            List<String> cacheNames = new ArrayList<>();
            if (expires != null){
                for (Map.Entry<String, Long> expire : expires.entrySet()){
                    log.info("User-Defined Cache: '{}' with expiration time: {} seconds", expire.getKey(), expire.getValue());
                    cacheNames.add(expire.getKey());
                }
            }
            if (!cacheNames.isEmpty()) {
                cacheManager.setCacheNames(cacheNames);
                cacheManager.setExpires(expires);
            }
            return cacheManager;
        }
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append(".");
            sb.append(method.getName());
            sb.append(":");

            if (objects != null) {
                sb.append(JsonUtils.toJson(objects, JsonUtils.getObjectMapperNonEmpty()));
            }
                return sb.toString();
        };
    }

}
