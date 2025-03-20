package com.att.popcornPalace;


import com.att.tdp.popcornPalace.PopcornPalaceApplication;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest(classes = PopcornPalaceApplication.class)
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @AfterEach
    public void cleanup() {
        movieRepository.deleteAll();
    }

    @Test
    public void testMovieRepository() {
        // Create and save a movie
        Movie movie = new Movie(null, "Inception", "Sci-Fi", 148, 8.8, 2010);
        Movie savedMovie = movieRepository.save(movie);
        assertNotNull(savedMovie.getId(), "Movie ID should not be null after saving");

        // Find the movie by title
        Optional<Movie> foundMovie = movieRepository.findByTitle("Inception");
        assertTrue(foundMovie.isPresent(), "Movie should be found");
        assertEquals("Inception", foundMovie.get().getTitle());
    }
    @Test
    public void testAddMovieToH2AndVerify() {
        // Arrange: Create a new movie
        Movie movie = new Movie(null, "Interstellar4333", "Sci-Fi", 169, 8.6, 2014);

        // Act: Save the movie to the database
        Movie savedMovie = movieRepository.save(movie);

        // Assert: Verify the movie was saved
        assertNotNull(savedMovie.getId(), "Movie ID should not be null after saving");

        // Fetch the movie from the database and verify
        Optional<Movie> foundMovie = movieRepository.findById(savedMovie.getId());
        System.out.println(foundMovie + " foundMovie");
        assertTrue(foundMovie.isPresent(), "Movie should be found in the database");
        assertEquals("Interstellar4333", foundMovie.get().getTitle(), "The movie title should match");

        List<Movie> allMovies = movieRepository.findAll();
        System.out.println("All movies in the database:");
        allMovies.forEach(System.out::println);
    }
    @Test
    public void testMovieExistsByTitle() {
        // Arrange: Create and save a new movie
        Movie movie = new Movie(null, "The Dark Knight", "Action", 152, 9.0, 2008);
        movieRepository.save(movie);

        // Act: Check if the movie exists by title
        boolean exists = movieRepository.existsByTitle("The Dark Knight");

        // Assert: Ensure the movie exists
        assertTrue(exists, "Movie should exist by title");
    }
    @Test
    public void testDeleteMovie() {
        // Arrange: Create and save a new movie
        Movie movie = new Movie(null, "The Matrix", "Sci-Fi", 136, 8.7, 1999);
        Movie savedMovie = movieRepository.save(movie);

        // Act: Delete the movie from the repository
        movieRepository.delete(savedMovie);

        // Assert: Verify the movie is deleted and no longer exists
        Optional<Movie> foundMovie = movieRepository.findById(savedMovie.getId());
        assertFalse(foundMovie.isPresent(), "Movie should not be found after deletion");
    }
    @Test
    public void testUpdateMovie() {
        // Arrange: Create and save a new movie
        Movie movie = new Movie(null, "Avatar", "Action", 162, 8.0, 2009);
        Movie savedMovie = movieRepository.save(movie);

        // Act: Update the movie
        savedMovie.setRating(8.5); // Change the rating
        Movie updatedMovie = movieRepository.save(savedMovie);

        // Assert: Verify the movie was updated
        assertEquals(8.5, updatedMovie.getRating(), "The movie rating should be updated");
    }

}