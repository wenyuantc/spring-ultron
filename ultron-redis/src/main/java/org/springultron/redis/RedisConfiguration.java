package org.springultron.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springultron.core.jackson.UltronJavaTimeModule;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Redis及Redis cache缓存管理器配置
 * 序列化策略：
 * 默认使用Jackson序列化Redis value，当没有依赖Jackson时默认使用jdk序列化
 *
 * @author brucewuu
 * @date 2019-05-31 14:26
 */
@Configuration
@AutoConfigureBefore({RedisAutoConfiguration.class})
public class RedisConfiguration extends CachingConfigurerSupport {

    /**
     * 自定义的缓存key的生成策略(消息队列 暂时用不到 自行忽略)
     * 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
     * 若想使用这个key只需要将注解上keyGenerator的值设置为keyGenerator即可
     *
     * @return 自定义策略生成的key
     */
    @Bean
    @ConditionalOnMissingBean(name = {"keyGenerator"})
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * 自定义Jackson序列化
     * 可自定义注入redisSerializer Bean替换
     */
    @Bean
    @ConditionalOnClass({ObjectMapper.class})
    @ConditionalOnMissingBean(name = {"redisSerializer"})
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 反序列化时去掉多余的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // java8日期序列化
        objectMapper.findAndRegisterModules();
        objectMapper.registerModule(new UltronJavaTimeModule());
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    //    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        // 反序列化时去掉多余的字段
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        return jackson2JsonRedisSerializer;
//    }

    /**
     * 配置自定义redisTemplate
     *
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, @Autowired(required = false) RedisSerializer<Object> redisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        // 使用StringRedisSerializer.UTF_8来序列化和反序列化redis的key值
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(redisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器(替换默认的 RedisCacheManager)
     * 需手动配置 @EnableCaching开启缓存
     *
     * @param redisConnectionFactory redis连接工厂
     * @return CacheManager
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Primary
    @Bean
    @ConditionalOnBean({CacheAspectSupport.class})
    @ConditionalOnMissingBean(name = {"cacheManager"})
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, @Autowired(required = false) RedisSerializer<Object> redisSerializer) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues();
        if (redisSerializer != null) {
            redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
        }
        CacheEnum[] cacheEnums = CacheEnum.values();
        Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheEnums.length);
        for (CacheEnum cacheEnum : cacheEnums) {
            cacheConfigMap.put(cacheEnum.getCacheName(), redisCacheConfiguration.entryTtl(Duration.ofSeconds(cacheEnum.getExpireTime())));
        }
        // 使用自定义的缓存配置初始化cacheManager
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration.entryTtl(Duration.ofSeconds(600))) // 默认策略，未配置的 key 会使用这个
                .withInitialCacheConfigurations(cacheConfigMap)
                .build();
    }
}