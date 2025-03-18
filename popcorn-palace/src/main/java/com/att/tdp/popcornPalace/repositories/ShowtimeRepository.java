package com.att.tdp.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s " +
            "WHERE s.theater = :theater " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime) " +
            "OR (s.startTime = :startTime AND s.endTime = :endTime))")
    List<Showtime> findOverlappingShowtimes(String theater, LocalDateTime startTime, LocalDateTime endTime);
}
