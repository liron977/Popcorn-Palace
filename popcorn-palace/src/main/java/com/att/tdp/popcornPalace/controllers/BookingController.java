package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.models.Booking;
import com.att.tdp.popcornPalace.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    public ResponseEntity<?> bookTicket(@Valid @RequestBody Booking booking, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : result.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
        }

       try {
            if (booking.getShowtimeId() == null) {
                return ResponseEntity.badRequest().body("Showtime is missing or invalid.");
            }

            String bookingId = bookingService.bookTicket(booking.getShowtimeId(), booking.getSeatNumber(), booking.getUserId());

            Map<String, String> response = new HashMap<>();
            response.put("bookingId", bookingId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}