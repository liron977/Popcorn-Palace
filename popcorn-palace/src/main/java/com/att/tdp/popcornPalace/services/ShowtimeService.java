package com.att.tdp.popcornPalace.services;

import com.att.tdp.popcornPalace.dto.ShowtimeRequestDto;
import com.att.tdp.popcornPalace.dto.ShowtimeResponseDto;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));
    }



    public void deleteShowtime(Long showtimeId) {
        // First check if the showtime exists
        showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));

        showtimeRepository.deleteById(showtimeId);
    }


    public Showtime addShowtime(ShowtimeRequestDto request) {
        validateShowtime(request, null);

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", request.getMovieId().toString()));

        // Convert DTO to Entity
        Showtime showtime = Showtime.builder()
                .movie(movie)  // Set Movie object, not just ID
                .price(request.getPrice())
                .theater(request.getTheater())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return showtimeRepository.save(showtime);
    }


    public void updateShowtime(Long showtimeId, ShowtimeRequestDto showtimeDetails) {
        // Verify showtime exists
        Showtime existingShowtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));

        // Fetch the Movie object using the movieId from the ShowtimeRequestDto
        Movie movie = movieRepository.findById(showtimeDetails.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", showtimeDetails.getMovieId().toString()));

        // Validate business rules
        validateShowtime(showtimeDetails, showtimeId);

        // Update using builder pattern (requires @Builder on Showtime entity)
        Showtime updatedShowtime = Showtime.builder()
                .id(existingShowtime.getId()) // Keep the existing ID
                .movie(movie) // Directly set the Movie object
                .price(showtimeDetails.getPrice())
                .theater(showtimeDetails.getTheater())
                .startTime(showtimeDetails.getStartTime())
                .endTime(showtimeDetails.getEndTime())
                .build();

        showtimeRepository.save(updatedShowtime);

    }



    private void validateShowtime(ShowtimeRequestDto showtime, Long currentShowtimeId) {
        // Check if the movie exists

        movieRepository.findById(showtime.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", showtime.getMovieId().toString()));

        // Validate that the start time is before the end time
        if (!showtime.getStartTime().isBefore(showtime.getEndTime())) {
            throw new BusinessRuleViolationException("Start time must be before end time.");
        }

        // Check for overlapping showtimes
        validateNoOverlappingShowtimes(showtime, currentShowtimeId);
    }


    private void validateNoOverlappingShowtimes(ShowtimeRequestDto showtime, Long currentShowtimeId) {
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime()
        );

        Optional<Showtime> conflictingShowtime = overlappingShowtimes.stream()
                .filter(existingShowtime -> !existingShowtime.getId().equals(currentShowtimeId))
                .findFirst();

        // If there is an overlap, include details of the conflicting showtime
        if (conflictingShowtime.isPresent()) {
            Showtime overlap = conflictingShowtime.get();
            throw new BusinessRuleViolationException(String.format(
                    "Showtime overlaps with another show in theater %s. Conflicting showtime: %s to %s",
                    overlap.getTheater(),
                    overlap.getStartTime(),
                    overlap.getEndTime()
            ));
        }
    }

    public ShowtimeResponseDto mapToShowtimeResponseDto(Showtime showtime) {
        return new ShowtimeResponseDto(
                showtime.getId(),
                showtime.getPrice(),
                showtime.getMovie().getId(),  // Get movieId
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime()
        );
    }
    public Showtime findById(Long id) {
        Optional<Showtime> showtime = showtimeRepository.findById(id);
        if (showtime.isPresent()) {
            return showtime.get();
        } else {
            return null;  // Or you could throw an exception if you want to handle not finding a showtime
        }
    }

}