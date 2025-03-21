package com.att.popcornPalace.services;

import com.att.tdp.popcornPalace.PopcornPalaceApplication;
import com.att.tdp.popcornPalace.exception.DuplicateResourceException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import com.att.tdp.popcornPalace.services.MovieService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.BookingRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import com.att.tdp.popcornPalace.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@Transactional
@SpringBootTest(classes = PopcornPalaceApplication.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private BookingService bookingService;

    private Long showtimeId;
    private int seatNumber;
    private UUID userId;

    @BeforeEach
    void setUp() {
        showtimeId = 1L;
        seatNumber = 10;
        userId = UUID.randomUUID();
    }

    @AfterEach
    public void cleanup() {
        bookingRepository.deleteAll();
        showtimeRepository.deleteAll();
    }

//    @Test
//    void testBookTicket_Success() {
//        // Arrange
//        Showtime showtime = Showtime.builder().id(showtimeId).build();
//        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
//        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(false);
//
//        UUID generatedBookingId = UUID.randomUUID(); // Ensure a valid UUID is assigned
//        Booking savedBooking = Booking.builder()
//                .bookingId(generatedBookingId)  // Explicitly setting bookingId
//                .showtimeId(showtimeId)
//                .seatNumber(seatNumber)
//                .userId(userId)
//                .build();
//
//        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
//            Booking argument = invocation.getArgument(0);
//            argument.setBookingId(generatedBookingId); // Ensure it gets a valid ID
//            return argument;
//        });
//
//        // Act
//        String bookingId = bookingService.bookTicket(showtimeId, seatNumber, userId);
//
//        // Assert
//        assertNotNull(bookingId);
//        assertEquals(generatedBookingId.toString(), bookingId);  // Verify correct ID is returned
//        verify(bookingRepository).save(any(Booking.class));
//    }
//
//
//    @Test
//    void testBookTicket_ShowtimeNotFound() {
//        // Arrange
//        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
//                bookingService.bookTicket(showtimeId, seatNumber, userId)
//        );
//
//        assertEquals("Showtime with identifier " + showtimeId + " not found", exception.getMessage());
//
//    }
//
//    @Test
//    void testBookTicket_SeatAlreadyBooked() {
//        // Arrange
//        Showtime showtime = Showtime.builder().id(showtimeId).build();
//        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
//        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(true);
//
//        // Act & Assert
//        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
//                bookingService.bookTicket(showtimeId, seatNumber, userId)
//        );
//        assertEquals("Seat " + seatNumber + " is already booked for this showtime", exception.getMessage());
//    }
//
//    @Test
//    void testBookTicket_Failure_RepositorySaveThrowsException() {
//        // Arrange
//        Showtime showtime = Showtime.builder().id(showtimeId).build();
//        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
//        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(false);
//        when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                bookingService.bookTicket(showtimeId, seatNumber, userId)
//        );
//        assertEquals("Database error", exception.getMessage());
//    }
}