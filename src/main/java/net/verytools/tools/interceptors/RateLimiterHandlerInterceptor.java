package net.verytools.tools.interceptors;

import net.verytools.tools.*;
import net.verytools.tools.utils.BeanHolder;
import net.verytools.tools.utils.MethodSignatureCache;
import net.verytools.tools.utils.RateLimitRuleUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class RateLimiterHandlerInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    private final RedisRateLimiter redisRateLimiter;
    private final RateLimitRule config;
    private final BeanHolder<KeyResolver> keyResolverHolder;
    private final BeanHolder<PathPatternRateLimitRuleConfig> patternBeanHolder;
    private final BeanHolder<RateLimitResponseHandler> rateLimitRespHandlerHolder;
    private final MethodSignatureCache sigCache;
    private ApplicationContext ctx;

    public RateLimiterHandlerInterceptor(RedisRateLimiter redisRateLimiter,
                                         RateLimitRule config) {
        this.redisRateLimiter = redisRateLimiter;
        this.config = config;
        this.keyResolverHolder = new BeanHolder<>();
        this.rateLimitRespHandlerHolder = new BeanHolder<>();
        this.patternBeanHolder = new BeanHolder<>();
        this.sigCache = new MethodSignatureCache();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            KeyResolver defaultResolver = keyResolverHolder.get(KeyResolver.class, ctx);
            HandlerMethod m = (HandlerMethod) handler;
            RateLimit rateLimit = m.getMethodAnnotation(RateLimit.class);
            if (rateLimit != null) {
                return handlerScopedLimit(request, response, rateLimit, defaultResolver, m.getMethod());
            }

            // handle rate limit for prefixed path
            RateLimitRule rule = null;
            PathPatternRateLimitRuleConfig patternConfig = patternBeanHolder.get(PathPatternRateLimitRuleConfig.class, ctx);
            if (patternConfig != null) {
                rule = patternConfig.matchRule(request.getRequestURI());
            }
            if (rule != null || this.config != null) {
                if (!(defaultResolver instanceof EmptyResolver)
                        && !this.redisRateLimiter.isAllowed(defaultResolver.resolve(request), rule == null ? this.config : rule)) {
                    RateLimitResponseHandler rateLimitRespHandler = rateLimitRespHandlerHolder.get(RateLimitResponseHandler.class, ctx);
                    rateLimitRespHandler.handle(response);
                    return false;
                }
            }
        }

        return true;
    }

    private boolean handlerScopedLimit(HttpServletRequest request,
                                       HttpServletResponse response,
                                       RateLimit rateLimit,
                                       KeyResolver defaultResolver,
                                       Method method) throws IOException {
        Class<? extends KeyResolver> resolverClazz = rateLimit.resolver();
        KeyResolver resolver = defaultResolver;
        if (resolverClazz != EmptyResolver.class) {
            resolver = this.ctx.getBean(resolverClazz);
        }
        if (resolver instanceof EmptyResolver) {
            return false;
        }
        String sig = sigCache.getSig(method);
        String id = resolver.resolve(request) + "@" + sig;
        if (!this.redisRateLimiter.isAllowed(id, RateLimitRuleUtil.asRateLimitRule(rateLimit))) {
            RateLimitResponseHandler rateLimitRespHandler = rateLimitRespHandlerHolder.get(RateLimitResponseHandler.class, ctx);
            rateLimitRespHandler.handle(response);
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

}
