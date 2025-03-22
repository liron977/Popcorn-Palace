package com.att.popcornPalace.controllers;

import com.att.tdp.popcornPalace.controllers.ShowtimeController;
import com.att.tdp.popcornPalace.dto.ShowtimeRequestDto;
import com.att.tdp.popcornPalace.dto.ShowtimeResponseDto;
import com.att.tdp.popcornPalace.exception.HttpGlobalExceptionHandler;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.services.ShowtimeService;
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
import static org.hamcrest.Matchers.containsString;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(HttpGlobalExceptionHandler.class)
@Transactional
public class ShowtimeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShowtimeService showtimeService;

    @InjectMocks
    private ShowtimeController showtimeController;

    private Showtime showtime;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(showtimeController)
                .setControllerAdvice(new HttpGlobalExceptionHandler()) // Handles exceptions
                .build();

        Movie movie = new Movie(1L, "Sample Movie", "Action", 120, 8.5, 2025);

        showtime = new Showtime(1L, 12.5, movie, "IMAX",
                LocalDateTime.of(2025, 3, 22, 19, 0),
                LocalDateTime.of(2025, 3, 22, 21, 0));
    }

    // Test GET /showtimes/{showtimeId}
    @Test
    public void testGetShowtimeById_Success() throws Exception {
        // Create a mock Movie object
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 150, 8.9, 2011);

        // Create a mock Showtime object and set the Movie
        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(12.5)
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .movie(movie) // Set the movie field
                .build();

        // Create the ShowtimeResponseDto object with required arguments
        ShowtimeResponseDto responseDto = new ShowtimeResponseDto(
                showtime.getId(),
                showtime.getPrice(),
                showtime.getMovie().getId(), // Now this will work because movie is set
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime()
        );

        // Mock the service method to return the Showtime object
        when(showtimeService.getShowtimeById(1L)).thenReturn(showtime);
        when(showtimeService.mapToShowtimeResponseDto(showtime)).thenReturn(responseDto);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L)) // Adjusted path to account for 'data'
                .andExpect(jsonPath("$.price").value(12.5))
                .andExpect(jsonPath("$.theater").value("IMAX"));

        // Verify that the service methods were called
        verify(showtimeService, times(1)).getShowtimeById(1L);
        verify(showtimeService, times(1)).mapToShowtimeResponseDto(showtime);
    }



    // Test POST /showtimes
    @Test
    public void testAddShowtime_Success() throws Exception {

        ShowtimeRequestDto showtimeRequest = new ShowtimeRequestDto(1L, 12.5, "IMAX",
                LocalDateTime.parse("2025-03-22T19:00:00"), LocalDateTime.parse("2025-03-22T21:00:00"));

        Movie movie = new Movie(1L, "Sample Movie", "Action", 120, 8.5, 2025);
        Showtime showtime = new Showtime(1L, 12.5, movie, "IMAX",
                LocalDateTime.parse("2025-03-22T19:00:00"), LocalDateTime.parse("2025-03-22T21:00:00"));

        ShowtimeResponseDto responseDto = new ShowtimeResponseDto(
                showtime.getId(),
                showtime.getPrice(),
                showtime.getMovie().getId(),
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime()
        );

        when(showtimeService.addShowtime(any(ShowtimeRequestDto.class))).thenReturn(showtime);
        when(showtimeService.mapToShowtimeResponseDto(showtime)).thenReturn(responseDto);

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "price": 12.5,
                            "movieId": 1,
                            "theater": "IMAX",
                            "startTime": "2025-03-22T19:00:00",
                            "endTime": "2025-03-22T21:00:00"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))  // Adjusted path to $.data.id
                .andExpect(jsonPath("$.price").value(12.5));

        verify(showtimeService, times(1)).addShowtime(any(ShowtimeRequestDto.class));
    }



    // Test POST /showtimes (Validation Failure)
    @Test
    public void testAddShowtime_InvalidData() throws Exception {

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "price": -5,
                    "movieId": 0,
                    "theater": "",
                    "startTime": null,
                    "endTime": null
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Price must be non-negative")))
                .andExpect(jsonPath("$.message", containsString("Movie ID must be a positive number")))
                .andExpect(jsonPath("$.message", containsString("Theater is required")))
                .andExpect(jsonPath("$.message", containsString("Start time is required")))
                .andExpect(jsonPath("$.message", containsString("End time is required")));
    }

    @Test
    public void testUpdateShowtime_Success() throws Exception {
        // Mock the service method to do nothing when updateShowtime is called
        doNothing().when(showtimeService).updateShowtime(eq(1L), any(ShowtimeRequestDto.class));

        // Prepare the JSON content for ShowtimeRequestDto
        String showtimeRequestDtoJson = """
        {
            "price": 15.0,
            "movieId": 2,
            "theater": "Dolby",
            "startTime": "2025-03-23T18:00:00",
            "endTime": "2025-03-23T20:30:00"
        }
        """;

        // Perform the POST request to update the showtime (change to POST)
        mockMvc.perform(post("/showtimes/update/{showtimeId}", 1L)  // Use POST instead of PUT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(showtimeRequestDtoJson))
                .andExpect(status().isOk());  // Expecting HTTP 200 OK response

        // Verify the service method was called with the correct parameters
        verify(showtimeService, times(1)).updateShowtime(eq(1L), any(ShowtimeRequestDto.class));
    }




    @Test
    public void testUpdateShowtime_NotFound() throws Exception {
        // Mock the service method to throw ResourceNotFoundException when updateShowtime is called
        doThrow(new ResourceNotFoundException("Showtime", "1"))
                .when(showtimeService).updateShowtime(eq(1L), any(ShowtimeRequestDto.class));

        // Prepare the JSON content for ShowtimeRequestDto
        String showtimeRequestDtoJson = """
            {
                "price": 15.0,
                "movieId": 2,
                "theater": "Dolby",
                "startTime": "2025-03-23T18:00:00",
                "endTime": "2025-03-23T20:30:00"
            }
            """;

        // Perform the PUT request to update the showtime and expect 404 Not Found status
        mockMvc.perform(post("/showtimes/update/{showtimeId}", 1L)  // Use PUT instead of POST for update
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(showtimeRequestDtoJson))
                .andExpect(status().isNotFound());  // Expecting HTTP 404 Not Found response

        // Verify that the service method was called with the correct parameters
        verify(showtimeService, times(1)).updateShowtime(eq(1L), any(ShowtimeRequestDto.class));
    }

    // Test DELETE /showtimes/{showtimeId}
    @Test
    public void testDeleteShowtime_Success() throws Exception {
        doNothing().when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isOk());

        verify(showtimeService, times(1)).deleteShowtime(1L);
    }

    // Test DELETE /showtimes/{showtimeId} - Not Found
    @Test
    public void testDeleteShowtime_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Showtime", "1"))
                .when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isNotFound());

        verify(showtimeService, times(1)).deleteShowtime(1L);
    }
    // Test GET /showtimes/{showtimeId} - Not Found
    @Test
    public void testGetShowtimeById_NotFound() throws Exception {
        // Mock the service method to throw ResourceNotFoundException when showtimeId is not found
        when(showtimeService.getShowtimeById(1L)).thenThrow(new ResourceNotFoundException("Showtime", "1"));

        // Perform the GET request and expect 404 Not Found
        mockMvc.perform(get("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isNotFound()) // Expecting HTTP 404 Not Found response
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Showtime with identifier 1 not found"));

        // Verify the service method was called
        verify(showtimeService, times(1)).getShowtimeById(1L);
    }
    // Test POST /showtimes/update/{showtimeId} - Invalid Input
    @Test
    public void testUpdateShowtime_InvalidData() throws Exception {
        String invalidShowtimeRequestDtoJson = """
    {
        "price": -5,
        "movieId": 0,
        "theater": "",
        "startTime": null,
        "endTime": null
    }
    """;

        // Perform the POST request with invalid data
        mockMvc.perform(post("/showtimes/update/{showtimeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidShowtimeRequestDtoJson))
                .andExpect(status().isBadRequest()) // Expecting HTTP 400 Bad Request
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Price must be non-negative")))
                .andExpect(jsonPath("$.message", containsString("Movie ID must be a positive number")))
                .andExpect(jsonPath("$.message", containsString("Theater is required")))
                .andExpect(jsonPath("$.message", containsString("Start time is required")))
                .andExpect(jsonPath("$.message", containsString("End time is required")));
    }

    // Test GET /showtimes/{showtimeId} - Invalid showtimeId
    @Test
    public void testGetShowtimeById_InvalidId() throws Exception {
        // Perform the GET request with an invalid showtimeId (e.g., non-numeric)
        mockMvc.perform(get("/showtimes/{showtimeId}", "invalidId"))
                .andExpect(status().isInternalServerError()) // Expecting HTTP 500 Internal Server Error
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("Unexpected error occurred: Method parameter 'showtimeId': Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"invalidId\""));
    }
    // Test Exception Handling in HttpGlobalExceptionHandler
    @Test
    public void testGlobalExceptionHandler() throws Exception {
        // Mock a generic exception in the service
        when(showtimeService.getShowtimeById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        // Perform the GET request and expect the global exception handler to respond with a 500 error
        mockMvc.perform(get("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isInternalServerError()) // Expecting HTTP 500 Internal Server Error
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected error occurred: Unexpected error"));

        // Verify that the service method was called
        verify(showtimeService, times(1)).getShowtimeById(1L);
    }
    // Test POST /showtimes/update/{showtimeId} - Missing Fields (Bad Request)
    @Test
    public void testUpdateShowtime_MissingFields() throws Exception {
        String missingFieldsJson = """
    {
        "price": 15.0,
        "movieId": 2
    }
    """;

        // Perform the POST request with missing required fields (e.g., missing 'theater', 'startTime', and 'endTime')
        mockMvc.perform(post("/showtimes/update/{showtimeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingFieldsJson))
                .andExpect(status().isBadRequest()) // Expecting HTTP 400 Bad Request
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Theater is required")))
                .andExpect(jsonPath("$.message", containsString("Start time is required")))
                .andExpect(jsonPath("$.message", containsString("End time is required")));
    }






}
