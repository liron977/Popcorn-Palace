package com.att.popcornPalace.controllers;

import com.att.tdp.popcornPalace.controllers.ShowtimeController;
import com.att.tdp.popcornPalace.exception.GlobalExceptionHandler;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
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
import org.springframework.test.web.servlet.MvcResult;
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
@Import(GlobalExceptionHandler.class)
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
                .setControllerAdvice(new GlobalExceptionHandler()) // Handles exceptions
                .build();

        showtime = new Showtime(1L, 12.5, 1L, "IMAX",
                LocalDateTime.of(2025, 3, 22, 19, 0),
                LocalDateTime.of(2025, 3, 22, 21, 0));
    }

    // Test GET /showtimes/{showtimeId}
    @Test
    public void testGetShowtimeById_Success() throws Exception {
        when(showtimeService.getShowtimeById(1L)).thenReturn(showtime);

        mockMvc.perform(get("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(12.5))
                .andExpect(jsonPath("$.theater").value("IMAX"));

        verify(showtimeService, times(1)).getShowtimeById(1L);
    }

    // Test POST /showtimes
    @Test
    public void testAddShowtime_Success() throws Exception {
        when(showtimeService.addShowtime(any(Showtime.class))).thenReturn(showtime);

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
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(12.5));

        verify(showtimeService, times(1)).addShowtime(any(Showtime.class));
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

    // Test POST /showtimes/update/{showtimeId}
    @Test
    public void testUpdateShowtime_Success() throws Exception {
        doNothing().when(showtimeService).updateShowtime(eq(1L), any(Showtime.class));

        mockMvc.perform(post("/showtimes/update/{showtimeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "price": 15.0,
                                    "movieId": 2,
                                    "theater": "Dolby",
                                    "startTime": "2025-03-23T18:00:00",
                                    "endTime": "2025-03-23T20:30:00"
                                }
                                """))
                .andExpect(status().isOk());

        verify(showtimeService, times(1)).updateShowtime(eq(1L), any(Showtime.class));
    }

    // Test POST /showtimes/update/{showtimeId} - Not Found
    @Test
    public void testUpdateShowtime_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Showtime", "1"))
                .when(showtimeService).updateShowtime(eq(1L), any(Showtime.class));

        mockMvc.perform(post("/showtimes/update/{showtimeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "price": 15.0,
                                    "movieId": 2,
                                    "theater": "Dolby",
                                    "startTime": "2025-03-23T18:00:00",
                                    "endTime": "2025-03-23T20:30:00"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(showtimeService, times(1)).updateShowtime(eq(1L), any(Showtime.class));
    }

    // Test DELETE /showtimes/{showtimeId}
    @Test
    public void testDeleteShowtime_Success() throws Exception {
        doNothing().when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/showtimes/{showtimeId}", 1L))
                .andExpect(status().isNoContent());

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
}
