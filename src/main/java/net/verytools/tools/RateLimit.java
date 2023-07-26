package net.verytools.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int replenishRate() default 1;

    int burstCapacity() default 1;

    int requestedTokens() default 1;

    Class<? extends KeyResolver> resolver() default KeyResolver.class;

}