package com.att.tdp.popcornPalace.services;


import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.BookingRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
       showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));


        if (bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)) {
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