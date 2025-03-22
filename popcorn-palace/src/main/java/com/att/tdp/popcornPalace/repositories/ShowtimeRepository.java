package com.att.tdp.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s " +
            "WHERE s.theater = :theater " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime) " +
            "OR (s.startTime = :startTime AND s.endTime = :endTime))")
    List<Showtime> findOverlappingShowtimes(String theater, LocalDateTime startTime, LocalDateTime endTime);

    List<Showtime> findByMovieId(Long movieId);

    Optional<Showtime> findById(Long id);

}
