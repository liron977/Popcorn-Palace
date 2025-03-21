package com.att.popcornPalace.models;
import com.att.tdp.popcornPalace.models.Movie;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class MovieTest {

    private final Validator validator;

    public MovieTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidMovie() {
        Movie movie = Movie.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(8.8)
                .releaseYear(2010)
                .build();

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidMovie_BlankTitle() {
        Movie movie = Movie.builder()
                .title("")
                .genre("Sci-Fi")
                .duration(148)
                .rating(8.8)
                .releaseYear(2010)
                .build();

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Title cannot be blank"));
    }

    @Test
    void testInvalidMovie_NegativeDuration() {
        Movie movie = Movie.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(-10)
                .rating(8.8)
                .releaseYear(2010)
                .build();

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Duration should be a positive number"));
    }

    @Test
    void testInvalidMovie_InvalidRating() {
        Movie movie = Movie.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(-1)
                .releaseYear(2010)
                .build();

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Rating should be between 0 and 10"));
    }

    @Test
    void testInvalidMovie_InvalidReleaseYear() {
        Movie movie = Movie.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(8.8)
                .releaseYear(1800)
                .build();

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Release year should be greater than or equal to 1900"));
    }
}
