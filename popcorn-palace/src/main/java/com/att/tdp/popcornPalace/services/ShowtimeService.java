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

    /**
     * Constructor for ShowtimeService with required dependencies.
     *
     * @param showtimeRepository Repository for showtime data access
     * @param movieRepository Repository for movie data access
     */
    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    /**
     * Retrieves a showtime by its ID.
     *
     * @param showtimeId The ID of the showtime to retrieve
     * @return The showtime with the given ID
     * @throws ResourceNotFoundException If no showtime with the given ID exists
     */
    public Showtime getShowtimeById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));
    }

    /**
     * Deletes a showtime by its ID.
     *
     * @param showtimeId The ID of the showtime to delete
     * @throws ResourceNotFoundException If no showtime with the given ID exists
     */


    public void deleteShowtime(Long showtimeId) {
        // First check if the showtime exists
        showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId.toString()));

        showtimeRepository.deleteById(showtimeId);
    }

    /**
     * Creates a new showtime after validating business rules.
     *
     * @param request The showtimeDto to create
     * @return The created showtime with its generated ID
     * @throws ResourceNotFoundException If the referenced movie doesn't exist
     * @throws BusinessRuleViolationException If business rules are violated
     */
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

    /**
     * Updates an existing showtime after validating business rules.
     *
     * @param showtimeId The ID of the showtime to update
     * @param showtimeDetails The new details for the showtime
     * @throws ResourceNotFoundException If no showtime with the given ID exists or the referenced movie doesn't exist
     * @throws BusinessRuleViolationException If business rules are violated
     */
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


    /**
     * Validates the business rules for a showtime:
     * 1. The referenced movie must exist
     * 2. Start time must be before end time
     * 3. No overlapping showtimes in the same theater
     *
     * @param showtime The showtime to validate
     * @param currentShowtimeId The ID of the showtime being updated (null for new showtimes)
     * @throws ResourceNotFoundException If the referenced movie doesn't exist
     * @throws BusinessRuleViolationException If any business rule is violated
     */
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

    /**
     * Validates that the showtime doesn't overlap with any existing showtimes
     * in the same theater, excluding the current showtime (for updates).
     */
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

}