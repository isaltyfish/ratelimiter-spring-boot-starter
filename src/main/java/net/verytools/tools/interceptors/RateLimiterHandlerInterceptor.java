package net.verytools.tools.interceptors;

import net.verytools.tools.*;
import net.verytools.tools.utils.BeanHolder;
import net.verytools.tools.utils.MethodSignatureCache;
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
    private final RedisRateLimiterProperties config;
    private final BeanHolder<KeyResolver> keyResolverHolder;
    private final BeanHolder<RateLimitResponseHandler> rateLimitRespHandlerHolder;
    private final MethodSignatureCache sigCache;
    private ApplicationContext ctx;

    public RateLimiterHandlerInterceptor(RedisRateLimiter redisRateLimiter,
                                         RedisRateLimiterProperties config) {
        this.redisRateLimiter = redisRateLimiter;
        this.config = config;
        this.keyResolverHolder = new BeanHolder<>();
        this.rateLimitRespHandlerHolder = new BeanHolder<>();
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

            if (this.config.isGlobal()) {
                if (!(defaultResolver instanceof EmptyResolver)
                        && !this.redisRateLimiter.isAllowed(defaultResolver.resolve(request), this.config)) {
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
        RedisRateLimiterProperties p = new RedisRateLimiterProperties();
        p.setReplenishRate(rateLimit.replenishRate());
        p.setBurstCapacity(rateLimit.burstCapacity());
        p.setRequestedTokens(rateLimit.requestedTokens());
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
        if (!this.redisRateLimiter.isAllowed(id, p)) {
            RateLimitResponseHandler rateLimitRespHandler = rateLimitRespHandlerHolder.get(RateLimitResponseHandler.class, ctx);
            rateLimitRespHandler.handle(response);
            return false;
        }
        return true;
    }

    public static void renderString(HttpServletResponse response, String string) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(string);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
