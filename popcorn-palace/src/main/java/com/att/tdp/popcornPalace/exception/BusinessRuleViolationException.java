package com.att.tdp.popcornPalace.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends BaseApplicationException {
    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.CONFLICT);
    }
}