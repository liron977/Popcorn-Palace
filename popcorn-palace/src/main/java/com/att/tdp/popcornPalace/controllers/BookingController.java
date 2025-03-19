package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping
    public ResponseEntity<?> bookTicket(@Valid @RequestBody Booking booking) {
        if (booking.getShowtimeId() == null) {
            throw new BusinessRuleViolationException("Showtime is missing or invalid.");
        }

        String bookingId = bookingService.bookTicket(
                booking.getShowtimeId(),
                booking.getSeatNumber(),
                booking.getUserId()
        );

        Map<String, String> response = new HashMap<>();
        response.put("bookingId", bookingId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}