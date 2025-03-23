package com.att.popcornPalace.services;

import com.att.tdp.popcornPalace.PopcornPalaceApplication;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.BookingRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import com.att.tdp.popcornPalace.services.BookingService;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;

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

    @Test
    void testBookTicket_Success() {
        // Create a movie object
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        // Create a showtime object with details to update
        Showtime showtime = Showtime.builder()
                .id(1L)
                .movie(movie)
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.now().plusDays(1)) // Ensure showtime is in the future
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2)) // Ensure the showtime ends after start time
                .build();

        // Arrange
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(false);

        UUID generatedBookingId = UUID.randomUUID(); // Ensure a valid UUID is assigned
        Booking savedBooking = Booking.builder()
                .bookingId(generatedBookingId)  // Explicitly setting bookingId
                .showtime(showtime)
                .seatNumber(seatNumber)
                .userId(userId)
                .build();

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking argument = invocation.getArgument(0);
            argument.setBookingId(generatedBookingId); // Ensure it gets a valid ID
            return argument;
        });

        // Act
        String bookingId = bookingService.bookTicket(showtimeId, seatNumber, userId);

        // Assert
        assertNotNull(bookingId);
        assertEquals(generatedBookingId.toString(), bookingId);  // Verify correct ID is returned
        verify(bookingRepository).save(any(Booking.class));
    }



    @Test
    void testBookTicket_ShowtimeNotFound() {
        // Arrange
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookingService.bookTicket(showtimeId, seatNumber, userId)
        );

        assertEquals("Showtime with identifier " + showtimeId + " not found", exception.getMessage());

    }

    @Test
    void testBookTicket_SeatAlreadyBooked() {
        // Arrange
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        // Ensure the showtime is in the future
        Showtime showtime = Showtime.builder()
                .id(showtimeId)
                .movie(movie)
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.now().plusDays(1))  // Set to a future time
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2)) // Set end time after start time
                .build();

        // Mock repository behavior
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(true);  // Seat is already booked

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
                bookingService.bookTicket(showtimeId, seatNumber, userId)
        );

        // Assert the exception message
        assertEquals("Seat " + seatNumber + " is already booked for this showtime", exception.getMessage());
    }



    @Test
    void testBookTicket_Failure_RepositorySaveThrowsException() {
        // Arrange
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        Showtime showtime = Showtime.builder()
                .id(showtimeId)
                .movie(movie)
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.of(2025, 3, 22, 18, 0))
                .endTime(LocalDateTime.of(2025, 3, 22, 20, 0)) // ✅ Ensure endTime is set
                .build();

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Cannot book a ticket for a past showtime."));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookingService.bookTicket(showtimeId, seatNumber, userId)
        );
        assertEquals("Cannot book a ticket for a past showtime.", exception.getMessage());
    }
    @Test
    void testBookTicket_PastShowtime() {
        // Arrange
        Showtime pastShowtime = Showtime.builder()
                .id(showtimeId)
                .startTime(LocalDateTime.of(2024, 3, 22, 18, 0)) // Past date
                .endTime(LocalDateTime.of(2024, 3, 22, 20, 0))
                .build();

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(pastShowtime));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
                bookingService.bookTicket(showtimeId, seatNumber, userId)
        );
        assertEquals("Cannot book a ticket for a past showtime.", exception.getMessage());
    }
    @Test
    void testBookTicket_InvalidSeatNumber() {
        int invalidSeatNumber = -1;

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(new Showtime()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookTicket(showtimeId, invalidSeatNumber, userId)
        );

        assertEquals("Invalid seat number.", exception.getMessage());
    }

    @Test
    void testBookTicket_NullShowtimeId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookTicket(null, seatNumber, userId)
        );
        assertEquals("Invalid showtime ID.", exception.getMessage());
    }

    @Test
    void testBookTicket_NullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookTicket(showtimeId, seatNumber, null)
        );
        assertEquals("Invalid user ID.", exception.getMessage());
    }




}