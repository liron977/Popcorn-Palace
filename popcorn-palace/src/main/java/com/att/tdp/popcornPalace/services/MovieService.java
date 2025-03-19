package com.att.tdp.popcornPalace.services;

import com.att.tdp.popcornPalace.exception.DuplicateResourceException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie) {
        // Check if the movie with the same title already exists
        if (movieRepository.existsByTitle(movie.getTitle())) {
            throw new DuplicateResourceException("Movie", movie.getTitle());
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(String movieTitle, Movie movieDetails) {
        if (!movieTitle.equals(movieDetails.getTitle()) && movieRepository.existsByTitle(movieDetails.getTitle())) {
            throw new DuplicateResourceException("Movie", movieDetails.getTitle());
        }

        return movieRepository.findByTitle(movieTitle).map(movie -> {
            movie.setTitle(movieDetails.getTitle());
            movie.setGenre(movieDetails.getGenre());
            movie.setDuration(movieDetails.getDuration());
            movie.setRating(movieDetails.getRating());
            movie.setReleaseYear(movieDetails.getReleaseYear());
            return movieRepository.save(movie);
        }).orElseThrow(() -> new ResourceNotFoundException("Movie", movieTitle));
    }

    public void deleteMovie(String movieTitle) {
        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", movieTitle));
        movieRepository.delete(movie);
    }
}
