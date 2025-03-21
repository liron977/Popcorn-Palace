package com.att.popcornPalace.models;
import com.att.tdp.popcornPalace.models.Booking;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Validator validator;

    public BookingTest() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBooking() {
        // Create a valid Booking object
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtimeId(1L)
                .seatNumber(10)
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there are no validation violations
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidShowtimeId() {
        // Create an invalid Booking object with a negative showtimeId
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtimeId(-1L)
                .seatNumber(10)
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for showtimeId
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Showtime ID must be a positive number")));
    }

    @Test
    void testMissingShowtimeId() {
        // Create a Booking object with a null showtimeId
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtimeId(null)
                .seatNumber(10)
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for the showtimeId being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Showtime ID is required")));
    }

    @Test
    void testInvalidSeatNumber() {
        // Create an invalid Booking object with a non-positive seatNumber
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtimeId(1L)
                .seatNumber(0) // Invalid seat number
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for seatNumber
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Seat number must be a positive integer")));
    }

    @Test
    void testMissingSeatNumber() {
        // Create a Booking object with a null seatNumber
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtimeId(1L)
                .seatNumber(0) // Invalid seat number
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for the seatNumber being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Seat number must be a positive integer")));
    }

    @Test
    void testMissingUserId() {
        // Create a Booking object with a null userId
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtimeId(1L)
                .seatNumber(10)
                .userId(null) // Missing userId
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for the userId being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("User ID is required")));
    }
}