package com.rahul.fluxlimit.aspect;

import com.rahul.fluxlimit.annotation.RateLimit;
import com.rahul.fluxlimit.exception.RateLimitExceededException;
import com.rahul.fluxlimit.model.RateLimitResult;
import com.rahul.fluxlimit.service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitingAspect {

    @Autowired
    private RateLimitingService rateLimitingService;

    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No active HTTP request context found for rate limiting.");
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String clientId = request.getHeader(rateLimit.keyHeader());
        if (clientId == null || clientId.isBlank()) {
            clientId = request.getRemoteAddr();
        }

        RateLimitResult result = rateLimitingService.checkLimit(
                clientId,
                rateLimit.capacity(),
                rateLimit.refillRate()
        );

        // Standard rate limit headers attach karo
        if (response != null) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.capacity()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remainingTokens()));
        }

        if (!result.allowed()) {
            throw new RateLimitExceededException("Too many requests. Limit exceeded for client: " + clientId);
        }

        return joinPoint.proceed();
    }
}