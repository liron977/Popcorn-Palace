package com.att.popcornPalace.controllers;


import com.att.tdp.popcornPalace.controllers.MovieController;
import com.att.tdp.popcornPalace.exception.DuplicateResourceException;
import com.att.tdp.popcornPalace.exception.HttpGlobalExceptionHandler;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(HttpGlobalExceptionHandler.class)
public class MovieControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private Movie movie;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(movieController)
                .setControllerAdvice(new HttpGlobalExceptionHandler())  // Add this line
                .build();
        // Create a test movie
        movie = new Movie(null, "Inception", "Sci-Fi", 148, 8.8, 2010);
    }

    // Test for getting all movies
    @Test
    public void testGetAllMovies() throws Exception {
        // Prepare the mock data
        Movie movie1 = new Movie(1L, "Inception", "Sci-Fi", 148, 8.8, 2010);
        Movie movie2 = new Movie(2L, "Interstellar", "Sci-Fi", 169, 8.6, 2014);
        List<Movie> movies = Arrays.asList(movie1, movie2);

        // Mock the service layer
        when(movieService.getAllMovies()).thenReturn(movies);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Inception"))   // Fix: Use indices
                .andExpect(jsonPath("$[1].title").value("Interstellar"));

        // Verify that the service was called
        verify(movieService, times(1)).getAllMovies();
    }



    // Test for adding a new movie
    @Test
    public void testAddMovie_Success() throws Exception {
        // Mock the service layer
        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Inception\", \"genre\":\"Sci-Fi\", \"duration\":148, \"rating\":8.8, \"releaseYear\":2010}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"));

        // Verify that the service was called
        verify(movieService, times(1)).addMovie(any(Movie.class));
    }

    // Test for adding a movie with a duplicate title
    @Test
    public void testAddMovie_DuplicateTitle() throws Exception {
        //mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();

        // Mock the service to throw DuplicateResourceException
        when(movieService.addMovie(any(Movie.class)))
                .thenThrow(new DuplicateResourceException("Movie", "Inception"));

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Inception\", \"genre\":\"Sci-Fi\", \"duration\":148, \"rating\":8.8, \"releaseYear\":2010}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.message").value("Movie with identifier Inception already exists"));

        verify(movieService, times(1)).addMovie(any(Movie.class));
    }

    // Test for updating a movie
    @Test
    public void testUpdateMovie_Success() throws Exception {
        // Prepare mock data
        String movieTitle = "Inception";
        Movie updatedMovie = new Movie(null, "Inception Updated", "Sci-Fi", 150, 8.9, 2011);

        // Mock the service layer to return the updated movie (this part isn't needed for the controller test)
        when(movieService.updateMovie(eq(movieTitle), any(Movie.class))).thenReturn(updatedMovie);

        // Perform the PUT request and assert the response
        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Inception Updated\", \"genre\":\"Sci-Fi\", \"duration\":150, \"rating\":8.9, \"releaseYear\":2011}"))
                .andExpect(status().isOk()); // Expect 204 No Content status

        // Verify that the service was called
        verify(movieService, times(1)).updateMovie(eq(movieTitle), any(Movie.class));
    }


    // Test for updating a movie that doesn't exist
    @Test
    public void testUpdateMovie_NotFound() throws Exception {
        String movieTitle = "Nonexistent Movie";

        // Mock the service layer to throw an exception
        when(movieService.updateMovie(eq(movieTitle), any(Movie.class)))
                .thenThrow(new ResourceNotFoundException("Movie", movieTitle));

        // Perform the PUT request and assert the response
        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Movie\", \"genre\":\"Action\", \"duration\":120, \"rating\":7.5, \"releaseYear\":2023}"))
                .andExpect(status().isNotFound());

        // Verify that the service was called
        verify(movieService, times(1)).updateMovie(eq(movieTitle), any(Movie.class));
    }

    // Test for deleting a movie
    @Test
    public void testDeleteMovie_Success() throws Exception {
        String movieTitle = "Inception";

        // Perform the DELETE request and assert the response
        mockMvc.perform(delete("/movies/{movieTitle}", movieTitle))
                .andExpect(status().isOk());

        // Verify that the service was called
        verify(movieService, times(1)).deleteMovie(movieTitle);
    }

    // Test for deleting a movie that does not exist
    @Test
    public void testDeleteMovie_NotFound() throws Exception {
        String movieTitle = "Nonexistent Movie";

        // Mock the service layer to throw an exception
        doThrow(new ResourceNotFoundException("Movie", movieTitle))
                .when(movieService).deleteMovie(movieTitle);

        // Perform the DELETE request and assert the response
        mockMvc.perform(delete("/movies/{movieTitle}", movieTitle))
                .andExpect(status().isNotFound());

        // Verify that the service was called
        verify(movieService, times(1)).deleteMovie(movieTitle);
    }
}

