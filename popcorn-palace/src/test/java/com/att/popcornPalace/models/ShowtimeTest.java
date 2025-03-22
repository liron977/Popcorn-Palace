package com.att.popcornPalace.models;

import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Transactional
class ShowtimeTest {

    private final Validator validator;

    private  Movie movie;

    public ShowtimeTest() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before("")
    public void setup() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2025);
    }

    @Test
    void testValidShowtime() {

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2025);

        // Create a valid Showtime object
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movie(movie)
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Set<ConstraintViolation<Showtime>> violations = validator.validate(showtime);

        // Assert that there are no validation violations
        assertTrue(violations.isEmpty());
    }

    @Test
    void testMissingTheater() {

        // Create a Showtime object with a null theater
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(10.0)
                .movie(movie)
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
                .movie(movie)
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
                .movie(movie)
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