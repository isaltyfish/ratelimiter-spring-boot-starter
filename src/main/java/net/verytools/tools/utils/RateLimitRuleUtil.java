package net.verytools.tools.utils;

import net.verytools.tools.RateLimit;
import net.verytools.tools.RedisRateLimiterProperties;
import net.verytools.tools.RedisRateLimiterRule;

import java.time.Duration;
import java.util.regex.Pattern;

public class RateLimitRuleUtil {

    static final Pattern windowPattern = Pattern.compile("^\\d+[sSmMhH]$");

    public static RedisRateLimiterRule asRateLimitRule(RedisRateLimiterProperties p) {
        RedisRateLimiterRule ret = new RedisRateLimiterRule();
        String window = p.getWindow();
        if (!windowPattern.matcher(window).matches()) {
            throw new RuntimeException("invalid window: %s, window must be a positive number followed by any of [s,S,m,M,h,H]");
        }
        Duration duration = Duration.parse("PT" + window);
        int seconds = (int) duration.getSeconds();
        ret.setReplenishRate(p.getWindowTokens());
        ret.setBurstCapacity(p.getBurstCapacity() * seconds);
        ret.setRequestedTokens(p.getRequestedTokens() * seconds);
        return ret;
    }

    public static RedisRateLimiterRule asRateLimitRule(RateLimit rateLimit) {
        RedisRateLimiterProperties p = new RedisRateLimiterProperties();
        p.setWindow(rateLimit.window());
        p.setWindowTokens(rateLimit.windowTokens());
        p.setBurstCapacity(rateLimit.burstCapacity());
        p.setRequestedTokens(rateLimit.requestedTokens());
        return asRateLimitRule(p);
    }

}
