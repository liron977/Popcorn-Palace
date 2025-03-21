package com.att.tdp.popcornPalace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShowtimeRequestDto {
    @NotNull(message = "Movie ID is required")
    @Min(value = 1, message = "Movie ID must be a positive number")
    private Long movieId;

    @Min(value = 0, message = "Price must be non-negative")
    private double price;

    @NotBlank(message = "Theater is required")
    private String theater;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
}
