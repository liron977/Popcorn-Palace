package com.att.tdp.popcornPalace.exception;


public class BusinessRuleViolationException extends BaseApplicationException {
    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}