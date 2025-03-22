package com.att.popcornPalace.models;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
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
class BookingTest {

    private Validator validator;

    public BookingTest() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBooking() {
        // Create a Showtime object
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(12.99)
                .movie(new Movie()) // Assuming the Movie class is valid
                .theater("Theater 1")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        // Create a valid Booking object
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtime(showtime) // Linking the Showtime object
                .seatNumber(10)
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there are no validation violations
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidShowtime() {
        // Create an invalid Booking object with a null showtime (null showtime should trigger the validation message)
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtime(null)  // Invalid showtime (null)
                .seatNumber(10)
                .userId(UUID.randomUUID())
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for the showtime being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("Showtime ID is required")));
    }

    @Test
    void testInvalidSeatNumber() {
        // Create an invalid Booking object with a non-positive seatNumber
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(12.99)
                .movie(new Movie()) // Assuming the Movie class is valid
                .theater("Theater 1")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtime(showtime)
                .seatNumber(0)  // Invalid seat number
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
        // Create a Booking object with a missing seatNumber (0 is invalid here, so it's also tested)
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(12.99)
                .movie(new Movie()) // Assuming the Movie class is valid
                .theater("Theater 1")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtime(showtime)
                .seatNumber(0)  // Invalid seat number
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
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(12.99)
                .movie(new Movie()) // Assuming the Movie class is valid
                .theater("Theater 1")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtime(showtime)
                .seatNumber(10)
                .userId(null)  // Missing userId
                .build();

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        // Assert that there is a validation violation for the userId being required
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals("User ID is required")));
    }
}
