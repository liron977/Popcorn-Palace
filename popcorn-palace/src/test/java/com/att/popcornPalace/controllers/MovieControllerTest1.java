//package com.att.popcornPalace.controllers;
//
//import com.att.tdp.popcornPalace.models.Movie;
//import com.att.tdp.popcornPalace.services.MovieService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.http.MediaType;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//
//@SpringBootTest  // This will load the full Spring context if necessary
//@AutoConfigureMockMvc  // Automatically configures MockMvc for you
//public class MovieControllerTest1 {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MovieService movieService;  // Mock the MovieService
//
//    private Movie movie;
//
//    @BeforeEach
//    public void setUp() {
//        movie = new Movie(null,"Inception", "Sci-Fi", 148, 8.8, 2010);
//    }
//
//    @Test
//    public void testGetAllMovies() throws Exception {
//        // Mock the service to return a list of movies
//        List<Movie> movies = Arrays.asList(movie, new Movie(null,"Avatar", "Sci-Fi", 162, 7.8, 2009));
//        when(movieService.getAllMovies()).thenReturn(movies);
//
//        // Perform GET request and verify the response
//        mockMvc.perform(get("/movies/all"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("Inception"))
//                .andExpect(jsonPath("$[1].title").value("Avatar"));
//    }
//
//    @Test
//    public void testAddMovie() throws Exception {
//        // Mock the service to return the added movie
//        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);
//
//        // Perform POST request to add a movie
//        mockMvc.perform(post("/movies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"title\":\"Inception\",\"genre\":\"Sci-Fi\",\"duration\":148,\"rating\":8.8,\"releaseYear\":2010}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Inception"))
//                .andExpect(jsonPath("$.genre").value("Sci-Fi"));
//    }
//
//    @Test
//    public void testUpdateMovie() throws Exception {
//        // Mock the service to return the updated movie
//        Movie updatedMovie = new Movie(null,"Inception", "Sci-Fi", 148, 9.0, 2010);
//        when(movieService.updateMovie(eq("Inception"), any(Movie.class))).thenReturn(updatedMovie);
//
//        // Perform POST request to update a movie
//        mockMvc.perform(post("/movies/update/{movieTitle}", "Inception")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"title\":\"Inception\",\"genre\":\"Sci-Fi\",\"duration\":148,\"rating\":9.0,\"releaseYear\":2010}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.rating").value(9.0));
//    }
//
//    @Test
//    public void testDeleteMovie() throws Exception {
//        // Mock the service to simulate movie deletion (void method)
//        doNothing().when(movieService).deleteMovie(anyString());
//
//        // Perform DELETE request to delete a movie
//        mockMvc.perform(delete("/movies/{movieTitle}", "Inception"))
//                .andExpect(status().isNoContent());
//    }
//}
