package com.att.tdp.popcornPalace.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDto  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @NotNull(message = "Showtime ID is required")
    @Min(value = 1, message = "Showtime ID must be a positive number")
    private Long showtimeId;

    @Min(value = 1, message = "Seat number must be a positive integer")
    @NotNull(message = "Seat number is required")
    private int seatNumber;

    @NotNull(message = "User ID is required")
    private UUID userId;
}