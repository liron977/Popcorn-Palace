package com.att.popcornPalace.services;


import com.att.tdp.popcornPalace.exception.*;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import com.att.tdp.popcornPalace.services.MovieService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.att.tdp.popcornPalace.exception.DuplicateResourceException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private Movie updatedMovie;


    @AfterEach
    public void cleanup() {
        movieRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        movie = new Movie(null,"Inception", "Sci-Fi", 148, 8.8, 2010);
        updatedMovie = new Movie(null,"Inception", "Sci-Fi", 150, 9.0, 2010);
    }

    @Test
    public void testGetAllMovies() {
        // Test if the getAllMovies() method correctly calls the repository's findAll method
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        var result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
        verify(movieRepository, times(1)).findAll();
    }

//    @Test
//    public void testAddMovie_Success() {
//        // Test if a new movie is added successfully
//        when(movieRepository.existsByTitle(movie.getTitle())).thenReturn(false);
//        when(movieRepository.save(movie)).thenReturn(movie);
//
//
//
//        Movie result = movieService.addMovie(movie);
//
//        assertNotNull(result);
//        assertEquals("Inception", result.getTitle());
//        verify(movieRepository, times(1)).existsByTitle(movie.getTitle());
//        verify(movieRepository, times(1)).save(movie);
//
//        List<Movie> allMovies = movieRepository.findAll();
//        System.out.println("All movies in the database:");
//        allMovies.forEach(System.out::println);
//    }

    @Test
    public void testAddMovie_Success() {
        // Test if a new movie is added successfully
        when(movieRepository.existsByTitle(movie.getTitle())).thenReturn(false);
        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieRepository.findAll()).thenReturn(Collections.singletonList(movie));  // Mock findAll to return the added movie

        Movie result = movieService.addMovie(movie);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(movieRepository, times(1)).existsByTitle(movie.getTitle());
        verify(movieRepository, times(1)).save(movie);

        // Now verify the list of movies after adding one
        List<Movie> allMovies = movieRepository.findAll();
        System.out.println("All movies in the database:");
        allMovies.forEach(System.out::println);

        // Verify that the list contains the added movie
        assertTrue(allMovies.contains(movie));
    }


    @Test
    public void testAddMovie_DuplicateTitle() {
        // Test if an exception is thrown when trying to add a movie with a duplicate title
        when(movieRepository.existsByTitle(movie.getTitle())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> movieService.addMovie(movie));

        verify(movieRepository, times(1)).existsByTitle(movie.getTitle());
        verify(movieRepository, never()).save(movie);  // Ensure save is not called
    }

    @Test
    public void testUpdateMovie_Success() {
        // Test if the movie is updated successfully
        when(movieRepository.existsByTitle(updatedMovie.getTitle())).thenReturn(false);
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        Movie result = movieService.updateMovie("Inception", updatedMovie);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals("Sci-Fi", result.getGenre());
        assertEquals(150, result.getDuration());
        assertEquals(9.0, result.getRating());
        assertEquals(2010, result.getReleaseYear());
        verify(movieRepository, times(1)).findByTitle("Inception");
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    public void testUpdateMovie_DuplicateTitle() {
        // Test if an exception is thrown when trying to update a movie to an existing title
        Movie newMovie = new Movie(null,"Avatar", "Sci-Fi", 162, 7.8, 2009);
        when(movieRepository.existsByTitle(newMovie.getTitle())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> movieService.updateMovie("Inception", newMovie));

        verify(movieRepository, times(1)).existsByTitle(newMovie.getTitle());
        verify(movieRepository, never()).save(any(Movie.class));  // Ensure save is not called
    }

    @Test
    public void testUpdateMovie_MovieNotFound() {
        // Test if an exception is thrown when the movie to be updated doesn't exist
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie("Inception", updatedMovie));

        verify(movieRepository, times(1)).findByTitle("Inception");
        verify(movieRepository, never()).save(any(Movie.class));  // Ensure save is not called
    }

    @Test
    public void testDeleteMovie_WithShowtimes_ThrowsConflictException() {
        // Create a movie object
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        // Create associated showtimes for the movie
        Showtime showtime1 = new Showtime();
        showtime1.setId(20L);
        showtime1.setTheater("Sample Theater 7");
        showtime1.setStartTime(LocalDateTime.parse("2023-02-14T11:47:46.125405"));
        showtime1.setEndTime(LocalDateTime.parse("2025-02-14T14:47:46.125405"));

        Showtime showtime2 = new Showtime();
        showtime2.setId(21L);
        showtime2.setTheater("Sample Theater 8");
        showtime2.setStartTime(LocalDateTime.parse("2023-02-14T11:47:46.125405"));
        showtime2.setEndTime(LocalDateTime.parse("2025-02-14T14:47:46.125405"));

        // Mock the repository methods
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByMovieId(movie.getId())).thenReturn(List.of(showtime1, showtime2)); // Return the associated showtimes

        // Call the method under test and assert that the exception is thrown
        ConflictException exception = assertThrows(ConflictException.class, () -> movieService.deleteMovie("Inception"));

        // Verify the error message contains the expected showtimes details
        String expectedMessage = "Movie has associated showtimes. Please delete the showtimes before deleting the movie. Associated showtimes:\n" +
                "Showtime ID: 20, Theater: Sample Theater 7, Start Time: 2023-02-14T11:47:46.125405, End Time: 2025-02-14T14:47:46.125405\n" +
                "Showtime ID: 21, Theater: Sample Theater 8, Start Time: 2023-02-14T11:47:46.125405, End Time: 2025-02-14T14:47:46.125405\n";

        assertEquals(expectedMessage, exception.getMessage());

        // Verify that the methods are called the expected number of times
        verify(movieRepository, times(1)).findByTitle("Inception");
        verify(showtimeRepository, times(1)).findByMovieId(movie.getId());
        verify(movieRepository, never()).delete(movie); // Ensure movie is not deleted
    }

    @Test
    public void testDeleteMovie_MovieNotFound() {
        // Test if an exception is thrown when the movie to be deleted is not found
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie("Inception"));

        verify(movieRepository, times(1)).findByTitle("Inception");
        verify(movieRepository, never()).delete(any(Movie.class));  // Ensure delete is not called
    }
}