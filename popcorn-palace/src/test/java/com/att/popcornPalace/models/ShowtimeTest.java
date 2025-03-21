package com.att.popcornPalace.models;

import com.att.tdp.popcornPalace.models.Showtime;
import org.junit.jupiter.api.Test;

import com.att.tdp.popcornPalace.models.Booking;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

class ShowtimeTest {

    private final Validator validator;

    public ShowtimeTest() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidShowtime() {
        // Create a valid Showtime object
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movieId(1L)
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there are no validation violations
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidPrice() {
        // Create an invalid Showtime object with a negative price
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(-5.0)  // Invalid price
                .movieId(1L)
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there is a validation violation for price
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Price must be non-negative")));
    }

    @Test
    void testInvalidMovieId() {
        // Create an invalid Showtime object with a non-positive movieId
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movieId(-1L)  // Invalid movieId
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there is a validation violation for movieId
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Movie ID must be a positive number")));
    }

    @Test
    void testMissingMovieId() {
        // Create a Showtime object with a null movieId
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movieId(null)  // Missing movieId
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there is a validation violation for the movieId being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Movie ID is required")));
    }

    @Test
    void testMissingTheater() {
        // Create a Showtime object with a null theater
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movieId(1L)
                .theater(null)  // Missing theater
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there is a validation violation for the theater being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Theater is required")));
    }

    @Test
    void testInvalidStartTime() {
        // Create a Showtime object with a null startTime
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movieId(1L)
                .theater("IMAX")
                .startTime(null)  // Missing startTime
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there is a validation violation for the startTime being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Start time is required")));
    }

    @Test
    void testInvalidEndTime() {
        // Create a Showtime object with a null endTime
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movieId(1L)
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(null)  // Missing endTime
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there is a validation violation for the endTime being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("End time is required")));
    }

}