package com.att.tdp.popcornPalace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @NotNull(message = "Showtime ID is required")
    @Min(value = 1, message = "Movie ID must be a positive number")
    private Long showtimeId;

    @Min(value = 1, message = "Seat number must be a positive integer")
    private int seatNumber;

    @NotNull(message = "User ID is required")
    private UUID userId;
}

