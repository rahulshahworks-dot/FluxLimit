package com.rahul.fluxlimit.model;

public record RateLimitResult(boolean allowed, long remainingTokens) {}
