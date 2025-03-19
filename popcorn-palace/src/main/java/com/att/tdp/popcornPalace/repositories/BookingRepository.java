package com.att.tdp.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, int seatNumber);
}
