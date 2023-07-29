package net.verytools.tools.utils;

import net.verytools.tools.RateLimit;
import net.verytools.tools.RedisRateLimiterProperties;
import net.verytools.tools.RateLimitRule;

import java.time.Duration;
import java.util.regex.Pattern;

public class RateLimitRuleUtil {

    static final Pattern windowPattern = Pattern.compile("^\\d+[sSmMhH]$");

    public static RateLimitRule asRateLimitRule(RedisRateLimiterProperties config) {
        RateLimitRule ret = new RateLimitRule();
        String window = config.getWindow();
        if (!windowPattern.matcher(window).matches()) {
            throw new RuntimeException("invalid window: %s, window must be a positive number followed by any of [s,S,m,M,h,H]");
        }
        Duration duration = Duration.parse("PT" + window);
        int seconds = (int) duration.getSeconds();
        ret.setReplenishRate(config.getWindowTokens());
        ret.setBurstCapacity(config.getBurstCapacity() * seconds);
        ret.setRequestedTokens(config.getRequestedTokens() * seconds);
        ret.setGlobal(config.isGlobal());
        return ret;
    }

    public static RateLimitRule asRateLimitRule(RateLimit rateLimit) {
        RedisRateLimiterProperties p = new RedisRateLimiterProperties();
        p.setWindow(rateLimit.window());
        p.setWindowTokens(rateLimit.windowTokens());
        p.setBurstCapacity(rateLimit.burstCapacity());
        p.setRequestedTokens(rateLimit.requestedTokens());
        return asRateLimitRule(p);
    }

}
