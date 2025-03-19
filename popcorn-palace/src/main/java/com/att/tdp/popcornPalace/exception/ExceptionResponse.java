package com.att.tdp.popcornPalace.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {
    // Getters
    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp;

    public ExceptionResponse(String errorCode, String message, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }

}
