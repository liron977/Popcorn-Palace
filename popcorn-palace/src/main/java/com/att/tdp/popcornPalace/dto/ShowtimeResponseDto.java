package com.att.tdp.popcornPalace.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowtimeResponseDto {
    private Long id;
    private Double price;
    private Long movieId;  // Use movieId instead of the full movie object
    private String theater;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Constructors, Getters, and Setters

    public ShowtimeResponseDto(Long id, Double price, Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.price = price;
        this.movieId = movieId;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
