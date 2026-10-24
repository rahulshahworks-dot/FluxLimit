package com.rahul.fluxlimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int capacity() default 5;       // Default max burst tokens
    int refillRate() default 1;     // Default refill tokens per second
    String keyHeader() default "X-User-Id"; // Header jisse user identify hoga
}
