package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.services.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

   public BookingController(BookingService bookingService){
       this.bookingService = bookingService;
   }

    @PostMapping
    public String bookTicket(@RequestParam Long showtimeId, @RequestParam int seatNumber, @RequestParam UUID userId) {
        try {
            return bookingService.bookTicket(showtimeId, seatNumber, userId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}
