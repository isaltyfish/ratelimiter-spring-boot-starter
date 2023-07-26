package net.verytools.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Arrays;
import java.util.List;

public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;
    @SuppressWarnings({"rawtypes"})
    private final DefaultRedisScript script;
    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        this.script = redisScript;
    }

    @SuppressWarnings("unchecked")
    public boolean isAllowed(String id, RedisRateLimiterProperties p) {
        // How many requests per second do you want a user to be allowed to do?
        int replenishRate = p.getReplenishRate();
        // How much bursting do you want to allow?
        int burstCapacity = p.getBurstCapacity();
        // How many tokens are requested per request?
        int requestedTokens = p.getRequestedTokens();

        try {
            List<String> keys = getKeys(id);
            // The arguments to the LUA script. time() returns unixtime in seconds.
            Object[] scriptArgs = {replenishRate + "", burstCapacity + "", "", requestedTokens + ""};
            // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
            List<Long> ret = (List<Long>) this.redisTemplate.execute(this.script, keys, scriptArgs);
            return ret != null && !ret.isEmpty() && ret.get(0) == 1;
        } catch (Exception e) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            logger.error("exec rate limit script error", e);
        }
        return false;
    }

    static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

}
