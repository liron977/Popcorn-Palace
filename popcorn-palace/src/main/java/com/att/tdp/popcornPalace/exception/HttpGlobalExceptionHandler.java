package com.att.tdp.popcornPalace.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class HttpGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpGlobalExceptionHandler.class);

    // see if you can make it handel only for http response and not other
    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleBaseApplicationException(BaseApplicationException ex,HttpServletRequest request) {
        logger.error("Application exception occurred: {}", ex.getMessage(), ex);

        HttpStatus status = mapExceptionToHttpStatus(ex);

        ExceptionResponse errorResponse = new ExceptionResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex,HttpServletRequest request) {
        String errorMessage = "Unexpected error occurred: " + ex.getMessage();

        logger.error(errorMessage, ex);

        ExceptionResponse errorResponse = new ExceptionResponse(
                "INTERNAL_SERVER_ERROR",
                errorMessage,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex,HttpServletRequest request) {
        logger.error("Illegal argument exception occurred: {}", ex.getMessage(), ex);

        ExceptionResponse errorResponse = new ExceptionResponse(
                "INVALID_INPUT",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleJsonParseError(HttpMessageNotReadableException ex,HttpServletRequest request) {
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
                request.getRequestURI()
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
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex,HttpServletRequest request) {
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
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private HttpStatus mapExceptionToHttpStatus(BaseApplicationException ex) {
        return switch (ex.getClass().getSimpleName()) {
            case "BusinessRuleViolationException" -> HttpStatus.CONFLICT;
            case "DeleteConflictException" -> HttpStatus.CONFLICT;
            case "ResourceNotFoundException" -> HttpStatus.NOT_FOUND;
            case "DuplicateResourceException" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}