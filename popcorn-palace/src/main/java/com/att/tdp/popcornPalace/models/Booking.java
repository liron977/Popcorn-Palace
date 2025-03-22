package com.att.tdp.popcornPalace.models;

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
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @NotNull(message = "Showtime ID is required")
    @ManyToOne
    @JoinColumn(name = "showtime_id", referencedColumnName = "id", nullable = false) // Foreign key to Showtime table
    private Showtime showtime;


    @Min(value = 1, message = "Seat number must be a positive integer")
    @NotNull(message = "Seat number is required")
    private Integer seatNumber;

    @NotNull(message = "User ID is required")
    private UUID userId;
}

