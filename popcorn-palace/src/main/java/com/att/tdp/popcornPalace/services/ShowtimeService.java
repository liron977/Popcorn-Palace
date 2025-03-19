package com.att.tdp.popcornPalace.services;

import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    public Optional<Showtime> getShowtimeById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId);
    }

    public boolean deleteShowtime(Long showtimeId) {
        if (showtimeRepository.existsById(showtimeId)) {
            showtimeRepository.deleteById(showtimeId);
            return true;
        }
        return false;
    }

    public Showtime addShowtime(Showtime showtime) {
        if (isOverlapping(showtime)) {
            throw new IllegalArgumentException("Showtime overlaps with another show in the same theater.");
        }
        return showtimeRepository.save(showtime);
    }

    private boolean isOverlapping(Showtime showtime) {
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime()
        );
        return !overlappingShowtimes.isEmpty();
    }

    public void updateShowtime(Long showtimeId, Showtime showtimeDetails) {
        showtimeRepository.findById(showtimeId).map(showtime -> {
            showtime.setMovieId(showtimeDetails.getMovieId());
            showtime.setPrice(showtimeDetails.getPrice());
            showtime.setTheater(showtimeDetails.getTheater());
            showtime.setStartTime(showtimeDetails.getStartTime());
            showtime.setEndTime(showtimeDetails.getEndTime());
            return showtimeRepository.save(showtime);
        }).orElseThrow(() -> new IllegalArgumentException("Showtime not found"));
    }
}
