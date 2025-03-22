package com.att.tdp.popcornPalace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Genre cannot be blank")
    private String genre;

    @Min(value = 1, message = "Duration should be a positive number")
    private int duration;

    @Min(value = 0, message = "Rating should be between 0 and 10")
    private double rating;

    @Min(value = 1900, message = "Release year should be greater than or equal to 1900")
    private int releaseYear;
}
