package net.verytools.tools;

import net.verytools.tools.interceptors.RateLimiterHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisRateLimiterProperties.class)
public class RateLimiterConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public RedisRateLimiter redisRateLimiter(StringRedisTemplate template) {
        return new RedisRateLimiter(template);
    }

    @ConditionalOnMissingBean(KeyResolver.class)
    @Bean
    public KeyResolver keyResolver() {
        return new EmptyResolver();
    }

    @Bean
    public RateLimiterHandlerInterceptor rateLimiterHandlerInterceptor(RedisRateLimiter redisRateLimiter,
                                                                       RedisRateLimiterProperties properties) {
        return new RateLimiterHandlerInterceptor(redisRateLimiter, properties);
    }
}
