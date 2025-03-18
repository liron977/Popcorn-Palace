package com.att.tdp.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByShowtimeAndSeatNumber(Showtime showtime, int seatNumber);
}
