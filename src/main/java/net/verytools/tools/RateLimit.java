package net.verytools.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    String window() default "1s";

    int windowTokens() default 1;

    int burstCapacity() default 10;

    int requestedTokens() default 1;

    Class<? extends KeyResolver> resolver() default KeyResolver.class;

}
