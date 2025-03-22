package com.att.tdp.popcornPalace.services;

import com.att.tdp.popcornPalace.exception.DeleteConflictException;
import com.att.tdp.popcornPalace.exception.DuplicateResourceException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public MovieService(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;

    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    final String MOVIE_RESOURCE_NAME = "Movie";

    public Movie addMovie(Movie movie) {
        if (movie == null || movie.getTitle() == null) {
            throw new IllegalArgumentException("Movie details or title cannot be null.");
        }
        // Check if the movie with the same title already exists
        if (movieRepository.existsByTitle(movie.getTitle())) {
            throw new DuplicateResourceException(MOVIE_RESOURCE_NAME, movie.getTitle());
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(String movieTitle, Movie movieDetails) {
        if (movieDetails == null || movieDetails.getTitle() == null) {
            throw new IllegalArgumentException("Movie details or title cannot be null.");
        }

        if (!movieTitle.equals(movieDetails.getTitle()) && movieRepository.existsByTitle(movieDetails.getTitle())) {
            throw new DuplicateResourceException(MOVIE_RESOURCE_NAME, movieDetails.getTitle());
        }

        return movieRepository.findByTitle(movieTitle).map(movie -> {
            movie.setTitle(movieDetails.getTitle());
            movie.setGenre(movieDetails.getGenre());
            movie.setDuration(movieDetails.getDuration());
            movie.setRating(movieDetails.getRating());
            movie.setReleaseYear(movieDetails.getReleaseYear());

            return movieRepository.save(movie);
        }).orElseThrow(() -> new ResourceNotFoundException(MOVIE_RESOURCE_NAME, movieTitle));
    }

    @Transactional
    public void deleteMovie(String movieTitle) {
        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new ResourceNotFoundException(MOVIE_RESOURCE_NAME, movieTitle));

        List<Showtime> showtimes = showtimeRepository.findByMovieId(movie.getId());
        if (!showtimes.isEmpty()) {
            String errorMessage = getStringBuilderDeleteError(showtimes);

            throw new DeleteConflictException(errorMessage.toString(), MOVIE_RESOURCE_NAME, movie.getTitle());
        }

        movieRepository.delete(movie);
    }

    private String getStringBuilderDeleteError(List<Showtime> showtimes) {
        StringBuilder errorMessage = new StringBuilder("Movie has associated showtimes. Please delete the showtimes before deleting the movie. Associated showtimes:\n");
        showtimes.forEach(showtime ->
                errorMessage.append(String.format("Showtime ID: %d, Theater: %s, Start Time: %s, End Time: %s\n",
                        showtime.getId(),
                        showtime.getTheater(),
                        showtime.getStartTime(),
                        showtime.getEndTime())));
        return errorMessage.toString();
    }
}
