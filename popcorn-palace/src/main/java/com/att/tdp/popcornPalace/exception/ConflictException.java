package com.att.tdp.popcornPalace.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends  BaseApplicationException {
    public ConflictException(String message,String resourceType, String identifier) {
        super(
                String.format(message, resourceType, identifier),
                "DUPLICATE_RESOURCE",
                HttpStatus.CONFLICT

        );

    }
}
