package com.att.popcornPalace.controllers;

import com.att.tdp.popcornPalace.PopcornPalaceApplication;
import com.att.tdp.popcornPalace.controllers.MovieController;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.services.MovieService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PopcornPalaceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")  // This ensures the 'test' profile is used
public class MovieControllerIntegrationTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MovieService movieService;  // Mock the service layer
//
//    @InjectMocks
//    private MovieController movieController;
//
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//    }

    @Test
    public void testAddMovie() throws Exception {

//        Movie movie = new Movie(3L, "Avatar3", "Action", 120, 8.5, 2020);
//
//        // Mock the movieService to return the movie we just created
//        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);
//
//        // Act & Assert: perform the POST request and check the response
//        mockMvc.perform(post("/movies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(movie)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(3))
//                .andExpect(jsonPath("$.title").value("Avatar3"))
//                .andExpect(jsonPath("$.genre").value("Action"))
//                .andExpect(jsonPath("$.duration").value(120))
//                .andExpect(jsonPath("$.rating").value(8.5))
//                .andExpect(jsonPath("$.releaseYear").value(2020));
//
//        // Assert: Verify the movie was added to the database
//        Movie savedMovie = entityManager.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class)
//                .setParameter("title", "Avatar3")
//                .getSingleResult();
//
//        Assertions.assertNotNull(savedMovie);  // Assert that the movie was saved
//        Assertions.assertEquals("Avatar1", savedMovie.getTitle());  // Verify title
//        Assertions.assertEquals("Action", savedMovie.getGenre());  // Verify genre
//        Assertions.assertEquals(2020, savedMovie.getReleaseYear());  // Verify release year
//        Assertions.assertEquals(8.5, savedMovie.getRating(), 0.0);  // Verify rating
    }

//    @Test
//    public void testUpdateMovie() throws Exception {
//        // Arrange
//        String movieTitle = "Avatar";
//        Movie updatedMovie = new Movie("Avatar", "Sci-Fi", 2010);
//        when(movieService.updateMovie(eq(movieTitle), any(Movie.class))).thenReturn(updatedMovie);
//
//        // Act & Assert
//        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitle)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedMovie)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Avatar"))
//                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
//                .andExpect(jsonPath("$.year").value(2010));
//    }
//
//    @Test
//    public void testDeleteMovie() throws Exception {
//        // Arrange
//        String movieTitle = "Avatar";
//
//        // Act & Assert
//        mockMvc.perform(delete("/movies/{movieTitle}", movieTitle))
//                .andExpect(status().isNoContent());
//
//        // Verify that the movieService.deleteMovie was called once with the movieTitle
//        verify(movieService, times(1)).deleteMovie(movieTitle);
//    }
}