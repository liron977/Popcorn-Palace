package com.att.tdp.popcornPalace.exception;


public class DeleteConflictException extends  BaseApplicationException {
    public DeleteConflictException(String message, String resourceType, String identifier) {
        super(
                String.format(message, resourceType, identifier),
                "DUPLICATE_RESOURCE"

        );

    }
}
