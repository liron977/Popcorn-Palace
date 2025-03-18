package com.att.tdp.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}

