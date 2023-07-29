package net.verytools.tools.utils;

import net.verytools.tools.RateLimit;
import net.verytools.tools.RedisRateLimiterProperties;
import net.verytools.tools.RateLimitRule;

import java.time.Duration;
import java.util.regex.Pattern;

public class RateLimitRuleUtil {

    static final Pattern windowPattern = Pattern.compile("^\\d+[sSmMhH]$");

    public static RateLimitRule asRateLimitRule(RedisRateLimiterProperties.Config config) {
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
        return ret;
    }

    public static RateLimitRule asRateLimitRule(RateLimit rateLimit) {
        RedisRateLimiterProperties.Config config = new RedisRateLimiterProperties.Config();
        config.setWindow(rateLimit.window());
        config.setWindowTokens(rateLimit.windowTokens());
        config.setBurstCapacity(rateLimit.burstCapacity());
        config.setRequestedTokens(rateLimit.requestedTokens());
        return asRateLimitRule(config);
    }

}
