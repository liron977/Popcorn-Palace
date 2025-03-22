package com.att.tdp.popcornPalace.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {
    private final String errorCode;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;

    public ExceptionResponse(String errorCode, String message, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

}
