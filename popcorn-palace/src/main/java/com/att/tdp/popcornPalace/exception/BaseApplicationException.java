package com.att.tdp.popcornPalace.exception;

import lombok.Getter;

@Getter
public abstract class BaseApplicationException extends RuntimeException {
    private final String errorCode;

    protected BaseApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}