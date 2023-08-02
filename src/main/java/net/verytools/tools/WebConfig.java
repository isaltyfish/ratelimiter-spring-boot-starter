package net.verytools.tools;

import net.verytools.tools.interceptors.RateLimiterHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    RateLimiterHandlerInterceptor rateLimiterHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimiterHandlerInterceptor)
                .addPathPatterns("/**");
    }

}
