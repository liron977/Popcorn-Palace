package com.att.tdp.popcornPalace.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({BaseApplicationException.class,DuplicateResourceException.class,BusinessRuleViolationException.class,ResourceNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleBaseApplicationException(BaseApplicationException ex) {
        logger.error("Application exception occurred: {}", ex.getMessage(), ex);

        ExceptionResponse errorResponse = new ExceptionResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        String errorMessage = "Unexpected error occurred: " + ex.getMessage();

        logger.error(errorMessage, ex);

        ExceptionResponse errorResponse = new ExceptionResponse(
                "INTERNAL_SERVER_ERROR",
                errorMessage,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument exception occurred: {}", ex.getMessage(), ex);

        ExceptionResponse errorResponse = new ExceptionResponse(
                "INVALID_INPUT",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        logger.error("JSON parse error occurred: {}", ex.getMessage(), ex);

        // Get the root cause and extract useful information
        Throwable rootCause = ex.getMostSpecificCause();
        String errorMessage;

        if (rootCause instanceof JsonParseException jpe) {
            // Handle basic JSON syntax errors
            JsonLocation location = jpe.getLocation();
            errorMessage = String.format(
                    "Invalid JSON format at line %d, column %d: %s",
                    location.getLineNr(),
                    location.getColumnNr(),
                    simplifyJsonErrorMessage(jpe.getOriginalMessage())
            );
        } else if (rootCause instanceof JsonMappingException jme) {
            // Handle JSON value mapping errors
            String path = jme.getPath().isEmpty() ? "" :
                    " for field '" + jme.getPath().get(0).getFieldName() + "'";
            errorMessage = "Invalid value" + path + ": " +
                    simplifyJsonErrorMessage(jme.getOriginalMessage());
        } else {
            // Handle other JSON errors
            errorMessage = "Invalid input format: " + rootCause.getMessage();
        }

        ExceptionResponse errorResponse = new ExceptionResponse(
                "INVALID_JSON_FORMAT",
                errorMessage,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Helper method to simplify Jackson's verbose error messages
    private String simplifyJsonErrorMessage(String originalMessage) {
        if (originalMessage == null) return "Unknown error";

        // Handle common error messages more gracefully
        if (originalMessage.contains("Unrecognized token")) {
            return "Invalid value encountered in JSON";
        }
        if (originalMessage.contains("Cannot deserialize value of type")) {
            return originalMessage.replaceAll("Cannot deserialize value of type `[^`]+` from", "Expected");
        }

        return originalMessage;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation exception occurred: {}", ex.getMessage());

        // Extract the field errors and create a clean error message
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        // Create a clean, readable error message
        StringBuilder message = new StringBuilder("Validation failed: ");
        fieldErrors.forEach((field, errorMsg) ->
                message.append(field).append(" - ").append(errorMsg).append("; ")
        );

        ExceptionResponse errorResponse = new ExceptionResponse(
                "VALIDATION_ERROR",
                message.toString(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}