package com.att.tdp.popcornPalace.services;


import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.BookingRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {


    private final BookingRepository bookingRepository;

    private final  ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository,ShowtimeRepository showtimeRepository){
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public String bookTicket(Long showtimeId, int seatNumber, UUID userId) {
        Optional<Showtime> optionalShowtime = showtimeRepository.findById(showtimeId);
        if (optionalShowtime.isEmpty()) {
            throw new IllegalArgumentException("Showtime not found");
        }

        Showtime showtime = optionalShowtime.get();

        if (bookingRepository.existsByShowtimeAndSeatNumber(showtime, seatNumber)) {
            throw new IllegalStateException("Seat already booked");
        }

        Booking booking = Booking.builder()
                .showtime(showtime)
                .seatNumber(seatNumber)
                .userId(userId)
                .build();
        bookingRepository.save(booking);

        return booking.getBookingId().toString();
    }

}