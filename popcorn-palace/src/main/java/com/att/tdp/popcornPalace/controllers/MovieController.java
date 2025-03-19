package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

@PostMapping
public ResponseEntity<Movie> addMovie(@Valid @RequestBody Movie movie) {
    Movie addedMovie = movieService.addMovie(movie);
    return ResponseEntity.ok(addedMovie);
}
      @PostMapping("/update/{movieTitle}")
         public ResponseEntity<Movie> updateMovie(
                @PathVariable String movieTitle,
                 @Valid @RequestBody Movie movieDetails) {


            Movie updatedMovie = movieService.updateMovie(movieTitle, movieDetails);
             return ResponseEntity.ok(updatedMovie);
         }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.noContent().build();
    }
}