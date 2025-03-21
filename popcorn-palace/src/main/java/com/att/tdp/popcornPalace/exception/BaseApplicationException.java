package com.att.tdp.popcornPalace.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseApplicationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus; // ToDo: consider remove it from here

    protected BaseApplicationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}