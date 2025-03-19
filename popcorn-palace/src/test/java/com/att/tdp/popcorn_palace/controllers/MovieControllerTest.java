package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcornPalace.controllers.MovieController;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;


//@WebMvcTest(MovieController.class)
public class MovieControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MovieService movieService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private Movie testMovie;
//    private List<Movie> allMovies;
//
//    @BeforeEach
//    void setUp() {
//        testMovie = Movie.builder()
//                .id(1L)
//                .title("Test Movie")
//                .genre("Action")
//                .duration(120)
//                .rating(8.5)
//                .releaseYear(2023)
//                .build();
//
//        Movie anotherMovie = Movie.builder()
//                .id(2L)
//                .title("Another Movie")
//                .genre("Comedy")
//                .duration(95)
//                .rating(7.5)
//                .releaseYear(2022)
//                .build();
//
//        allMovies = Arrays.asList(testMovie, anotherMovie);
//    }
//
//    @Test
//    void getAllMovies_shouldReturnAllMovies() throws Exception {
//        when(movieService.getAllMovies()).thenReturn(allMovies);
//
//        mockMvc.perform(get("/movies/all"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].title", is("Test Movie")))
//                .andExpect(jsonPath("$[1].title", is("Another Movie")));
//
//        verify(movieService, times(1)).getAllMovies();
//    }
//
//    @Test
//    void addMovie_shouldReturnCreatedMovie() throws Exception {
//        when(movieService.addMovie(any(Movie.class))).thenReturn(testMovie);
//
//        mockMvc.perform(post("/movies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testMovie)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title", is("Test Movie")))
//                .andExpect(jsonPath("$.genre", is("Action")))
//                .andExpect(jsonPath("$.duration", is(120)))
//                .andExpect(jsonPath("$.rating", is(8.5)))
//                .andExpect(jsonPath("$.releaseYear", is(2023)));
//
//        verify(movieService, times(1)).addMovie(any(Movie.class));
//    }
//
//    @Test
//    void addMovie_withInvalidData_shouldReturnBadRequest() throws Exception {
//        Movie invalidMovie = Movie.builder()
//                .title("")  // Invalid: blank title
//                .genre("Action")
//                .duration(-10)  // Invalid: negative duration
//                .rating(8.5)
//                .releaseYear(1800)  // Invalid: year less than 1900
//                .build();
//
//        mockMvc.perform(post("/movies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidMovie)))
//                .andExpect(status().isBadRequest());
//
//        verify(movieService, never()).addMovie(any(Movie.class));
//    }
//
//    @Test
//    void updateMovie_shouldReturnUpdatedMovie() throws Exception {
//        Movie updatedMovie = Movie.builder()
//                .id(1L)
//                .title("Test Movie")
//                .genre("Updated Genre")
//                .duration(130)
//                .rating(9.0)
//                .releaseYear(2023)
//                .build();
//
//        when(movieService.updateMovie(eq("Test Movie"), any(Movie.class))).thenReturn(updatedMovie);
//
//        mockMvc.perform(post("/movies/update/{movieTitle}", "Test Movie")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedMovie)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.genre", is("Updated Genre")))
//                .andExpect(jsonPath("$.duration", is(130)))
//                .andExpect(jsonPath("$.rating", is(9.0)));
//
//        verify(movieService, times(1)).updateMovie(eq("Test Movie"), any(Movie.class));
//    }
//
//    @Test
//    void deleteMovie_shouldReturnNoContent() throws Exception {
//        doNothing().when(movieService).deleteMovie("Test Movie");
//
//        mockMvc.perform(delete("/movies/{movieTitle}", "Test Movie"))
//                .andExpect(status().isNoContent());
//
//        verify(movieService, times(1)).deleteMovie("Test Movie");
//    }
}