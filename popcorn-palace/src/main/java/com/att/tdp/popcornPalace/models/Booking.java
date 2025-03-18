package com.att.tdp.popcornPalace.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    private Showtime showtime;
    private int seatNumber;
    private UUID userId;

    public int getSeatNumber() {
        return seatNumber;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getUserId() {
        return userId;
    }
}

