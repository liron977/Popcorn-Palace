package com.att.popcornPalace.controllers;

import com.att.tdp.popcornPalace.controllers.BookingController;
import com.att.tdp.popcornPalace.exception.GlobalExceptionHandler;
import com.att.tdp.popcornPalace.models.Booking;
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
import java.util.UUID;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
@Transactional
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
                .setControllerAdvice(new GlobalExceptionHandler()) // Handles exceptions
                .build();

        Booking booking = Booking.builder()
                .showtimeId(1L)
                .seatNumber(1)
                .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        objectMapper = new ObjectMapper();
    }

    // Test POST /bookings - Success
    @Test
    public void testBookTicket_Success() throws Exception {
        when(bookingService.bookTicket(eq(1L), eq(1), eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))))
                .thenReturn(UUID.randomUUID().toString());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "showtimeId": 1,
                                    "seatNumber": 1,
                                    "userId": "123e4567-e89b-12d3-a456-426614174000"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").exists());

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


}

