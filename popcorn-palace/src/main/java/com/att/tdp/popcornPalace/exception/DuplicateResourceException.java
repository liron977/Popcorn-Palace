package com.att.tdp.popcornPalace.exception;


public class DuplicateResourceException extends BaseApplicationException {
    public DuplicateResourceException(String resourceType, String identifier) {
        super(
                String.format("%s with identifier %s already exists", resourceType, identifier),
                "DUPLICATE_RESOURCE"

        );
    }
}
