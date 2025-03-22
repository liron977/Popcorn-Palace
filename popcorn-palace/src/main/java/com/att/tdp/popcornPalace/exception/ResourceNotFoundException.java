package com.att.tdp.popcornPalace.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseApplicationException {
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
                String.format("%s with identifier %s not found", resourceType, identifier),
                "RESOURCE_NOT_FOUND"
        );
    }
}