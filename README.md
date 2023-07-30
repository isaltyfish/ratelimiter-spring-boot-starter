## Rate Limiter Spring Boot Starter

This is a redis-based rate limiter for spring boot project.

## Motivation

Spring Cloud supports Redis rate limiter. I hope that it can also be conveniently used in Spring Boot. Therefore, this project has ported the limiter from Spring Cloud Gateway to Spring Boot and added some encapsulation. Please refer to the features section for more details.

## Features

* Support global rate limiting
* Support API prefix rate limiting
* Support method-level rate limiting
* Support custom rate limiting objects, such as user ID and user IP.

## Quickstart

### step 1

```xml
<dependency>
    <groupId>net.verytools.tools</groupId>
    <artifactId>ratelimiter-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### step 2

Rate limiter relies on redis, a typical config for redis is like following:

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

To configure the rate limiter, edit the application.properties file and add these lines:

```properties
rate-limiter.config.window=2s
rate-limiter.config.windowTokens=1
rate-limiter.config.burstCapacity=3
rate-limiter.config.requestedTokens = 1
rate-limiter.global = true
```

* "rate-limiter.config.window" represents a time interval.
* "rate-limiter.config.windowTokens" represents the number of tokens to be added to the token bucket every "rate-limiter.config.window" configured time interval.
* "rate-limiter.config.burstCapacity" represents the capacity of the token bucket.
* "rate-limiter.config.requestedTokens" represents the number of tokens consumed per request.
* "rate-limiter.global" means whether the rate limiter for all requests should be enabled or not.

So, the meaning of the above configuration is: The bucket capacity is 3, 1 token is added to the bucket every 2 second, and each request consumes one token.

## step 3

Enable the rate limiter, add the `@EnableRateLimiter` annotation to your application's entry point class.

```java
@EnableRateLimiter
@SpringBootApplication
public class YourAwesomeApplication {
    public static void main(String[] args) {
    }
}
```

Use the RateLimiterHandlerInterceptor to intercept requests and configure a keyResolver to specify the rate limiting object, for example, limiting based on IP."

```java
import net.verytools.tools.KeyResolver;
import net.verytools.tools.interceptors.RateLimiterHandlerInterceptor;

@Configuration
public class RateLimiterConfig implements WebMvcConfigurer {

    @Resource
    RateLimiterHandlerInterceptor rateLimiterHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimiterHandlerInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    public KeyResolver keyResolver() {
        return new KeyResolver() {
            @Override
            public String resolve(HttpServletRequest req) {
                return req.getRemoteAddr();
            }
        };
    }

}
```
