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
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        ApiResponse<List<Movie>> response = new ApiResponse<>(movies);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody Movie movie) {
        Movie addedMovie = movieService.addMovie(movie);
        return ResponseEntity.ok(addedMovie);
    }

    //Delete operation should used PUT instead of POST , but I followed the api structure in the readme file
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable String movieTitle,
            @Valid @RequestBody Movie movieDetails) {

        movieService.updateMovie(movieTitle, movieDetails);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.noContent().build();
    }
}