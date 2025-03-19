package com.att.tdp.popcornPalace.services;

import com.att.tdp.popcornPalace.exception.MovieNotFoundException;
import com.att.tdp.popcornPalace.exception.ShowtimeNotFoundException;
import com.att.tdp.popcornPalace.exception.ShowtimeOverlapException;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    public Showtime getShowtimeById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ShowtimeNotFoundException("Showtime with ID " + showtimeId + " not found"));
    }

    public boolean deleteShowtime(Long showtimeId) {
        showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ShowtimeNotFoundException("Showtime with ID " + showtimeId + " not found"));
        showtimeRepository.deleteById(showtimeId);
        return true;
    }

    public Showtime addShowtime(Showtime showtime) {
        validateShowtime(showtime, null);

        return showtimeRepository.save(showtime);
    }

    private boolean isOverlapping(Showtime showtime) {
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime()
        );
        return !overlappingShowtimes.isEmpty();
    }

    public void updateShowtime(Long showtimeId, Showtime showtimeDetails) {
        Showtime existingShowtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ShowtimeNotFoundException("Showtime with ID " + showtimeId + " not found"));

        validateShowtime(showtimeDetails, showtimeId);

        // Update the showtime details
        existingShowtime.setMovieId(showtimeDetails.getMovieId());
        existingShowtime.setPrice(showtimeDetails.getPrice());
        existingShowtime.setTheater(showtimeDetails.getTheater());
        existingShowtime.setStartTime(showtimeDetails.getStartTime());
        existingShowtime.setEndTime(showtimeDetails.getEndTime());

        showtimeRepository.save(existingShowtime);
    }

    private void validateShowtime(Showtime showtime, Long currentShowtimeId) {
        // Check if the movie exists
        movieRepository.findById(showtime.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie with ID " + showtime.getMovieId() + " not found"));

        // Validate that the start time is before the end time
        if (!showtime.getStartTime().isBefore(showtime.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        // Validate if the showtime overlaps with other showtimes
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime()
        );

        boolean hasOverlap = overlappingShowtimes.stream()
                .anyMatch(existingShowtime -> !existingShowtime.getId().equals(currentShowtimeId));

        if (hasOverlap) {
            throw new ShowtimeOverlapException("Showtime overlaps with another show in the same theater.");
        }
    }

}
