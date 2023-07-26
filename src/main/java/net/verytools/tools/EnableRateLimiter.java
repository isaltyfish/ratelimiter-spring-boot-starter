package net.verytools.tools;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RateLimiterConfiguration.class})
@Documented
public @interface EnableRateLimiter {
}
