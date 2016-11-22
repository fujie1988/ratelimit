package com.xxx.ratelimit.exception;


public class RateLimitException extends RuntimeException {

    public RateLimitException(String msg) {
        super(msg);
    }
}

