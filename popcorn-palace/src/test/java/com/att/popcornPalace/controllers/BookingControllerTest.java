package com.att.popcornPalace.controllers;

import com.att.tdp.popcornPalace.controllers.BookingController;
import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.exception.HttpGlobalExceptionHandler;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.services.BookingService;
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

import java.time.LocalDateTime;
import java.util.UUID;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
@Import(HttpGlobalExceptionHandler.class)
public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(new HttpGlobalExceptionHandler()) // Handles exceptions
                .build();

        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 150, 8.9, 2011);


        Showtime showtime = Showtime.builder()
                .id(1L)
                .price(12.5)
                .theater("IMAX")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .movie(movie) // Set the movie field
                .build();

        Booking booking = Booking.builder()
                .showtime(showtime)
                .seatNumber(1)
                .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        objectMapper = new ObjectMapper();
    }

    // Test POST /bookings - Success
    @Test
    public void testBookTicket_Success() throws Exception {
        when(bookingService.bookTicket(eq(1L), eq(1), eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
                .thenReturn(UUID.randomUUID().toString());  // Simulating a generated bookingId

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "showtimeId": 1,
                                "seatNumber": 1,
                                "userId": "123e4567-e89b-12d3-a456-426614174000"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());  // Expect "bookingId" under "data"

        verify(bookingService, times(1)).bookTicket(eq(1L), eq(1), eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
    }



    // Test POST /bookings - Showtime Missing / Invalid
    @Test
    public void testBookTicket_ShowtimeIdMissing() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "seatNumber": 1,
                            "userId": "123e4567-e89b-12d3-a456-426614174000"
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Showtime ID is required")));

        verify(bookingService, times(0)).bookTicket(anyLong(), anyInt(), any());
    }


    // Test POST /bookings - Seat Number Invalid
    @Test
    public void testBookTicket_SeatNumberInvalid() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "showtimeId": 1,
                                "seatNumber": -1,
                                "userId": "123e4567-e89b-12d3-a456-426614174000"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Seat number must be a positive integer")));

        verify(bookingService, times(0)).bookTicket(anyLong(), anyInt(), any());
    }


    // Test POST /bookings - User ID Missing
    @Test
    public void testBookTicket_UserIdMissing() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "showtimeId": 1,
                                "seatNumber": 1
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("User ID is required")));

        verify(bookingService, times(0)).bookTicket(anyLong(), anyInt(), any());
    }

    // Test for missing Showtime ID
    @Test
    public void testBookTicket_ShowtimeIdInvalid() throws Exception {
        Booking booking = new Booking();
        booking.setSeatNumber(1);
        booking.setUserId(UUID.randomUUID());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Showtime ID is required")));

        // Verify that the bookingService was not called
        verify(bookingService, times(0)).bookTicket(anyLong(), anyInt(), any(UUID.class));
    }
    //  Test POST /bookings - Showtime is in the past
    @Test
    public void testBookTicket_PastShowtime() throws Exception {
        when(bookingService.bookTicket(anyLong(), anyInt(), any(UUID.class)))
                .thenThrow(new BusinessRuleViolationException("Cannot book a ticket for a past showtime."));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "showtimeId": 1,
                            "seatNumber": 5,
                            "userId": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """))
                .andExpect(status().isConflict())  // Expects 400
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_RULE_VIOLATION"))
                .andExpect(jsonPath("$.message").value("Cannot book a ticket for a past showtime."));

        verify(bookingService, times(1)).bookTicket(anyLong(), anyInt(), any(UUID.class));
    }



}

