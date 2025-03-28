package com.att.tdp.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    // Add a method to check if a movie with the same title already exists
    boolean existsByTitle(String title);
}

