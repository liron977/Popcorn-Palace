package com.att.tdp.popcornPalace.services;


import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.BookingRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {


    private final BookingRepository bookingRepository;

    private final  ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository,ShowtimeRepository showtimeRepository){
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional
    public String bookTicket(Long showtimeId, int seatNumber, UUID userId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));

        // Validate that the showtime is in the future
        if (showtime.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleViolationException("Cannot book a ticket for a past showtime.");
        }

        if (bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)) {
            // ToDo: consider adding a class per error, as BusinessRuleViolationException can be the parent exception
            throw new BusinessRuleViolationException("Seat " + seatNumber + " is already booked for this showtime");
        }

        Booking booking = Booking.builder()
                .showtimeId(showtimeId)
                .seatNumber(seatNumber)
                .userId(userId)
                .build();
        bookingRepository.save(booking);

        return booking.getBookingId().toString();
    }
}