package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    @PostMapping("/update/{movieId}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long movieId, @RequestBody Movie movieDetails) {
        return movieRepository.findById(movieId).map(movie -> {
            movie.setTitle(movieDetails.getTitle());
            movie.setGenre(movieDetails.getGenre());
            movie.setDuration(movieDetails.getDuration());
            movie.setRating(movieDetails.getRating());
            movie.setReleaseYear(movieDetails.getReleaseYear());
            return ResponseEntity.ok(movieRepository.save(movie));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        if (movieRepository.existsById(movieId)) {
            movieRepository.deleteById(movieId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
