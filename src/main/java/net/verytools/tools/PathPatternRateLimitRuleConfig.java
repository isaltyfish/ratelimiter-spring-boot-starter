package net.verytools.tools;

import net.verytools.tools.utils.RateLimitRuleUtil;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathPatternRateLimitRuleConfig {

    private final AntPathMatcher matcher;

    public PathPatternRateLimitRuleConfig() {
        this.matcher = new AntPathMatcher();
    }

    static class PathPatternRateLimitRule {
        private RateLimitRule rule;
        private List<String> pathPatterns = new ArrayList<>();

        public RateLimitRule getRule() {
            return rule;
        }

        public void setRule(RateLimitRule rule) {
            this.rule = rule;
        }

        public List<String> getPathPatterns() {
            return pathPatterns;
        }

        public void setPathPatterns(List<String> pathPatterns) {
            this.pathPatterns = pathPatterns;
        }
    }

    private final List<PathPatternRateLimitRule> rules = new ArrayList<>();

    public PathPatternRateLimitRuleConfig rule(RedisRateLimiterProperties config, String... pathPatterns) {
        if (pathPatterns != null) {
            PathPatternRateLimitRule r = new PathPatternRateLimitRule();
            RateLimitRule rateLimitRule = RateLimitRuleUtil.asRateLimitRule(config);
            rateLimitRule.setGlobal(false);
            r.setRule(rateLimitRule);
            r.setPathPatterns(Arrays.asList(pathPatterns));
            rules.add(r);
        }
        return this;
    }

    public RateLimitRule matchRule(String path) {
        for (PathPatternRateLimitRule rule : this.rules) {
            for (String pathPattern : rule.getPathPatterns()) {
                if (matcher.match(pathPattern, path)) {
                    return rule.getRule();
                }
            }
        }
        return null;
    }

}
