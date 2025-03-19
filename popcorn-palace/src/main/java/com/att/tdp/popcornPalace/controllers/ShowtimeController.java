package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.exception.ShowtimeNotFoundException;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.services.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    // Get Showtime by ID
    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getShowtimeById(@PathVariable Long showtimeId) {
        try {
            Showtime showtime = showtimeService.getShowtimeById(showtimeId);
            return ResponseEntity.ok(showtime);  // Returns 200 OK with the showtime
        } catch (ShowtimeNotFoundException e) {
            // Returns 404 Not Found if ShowtimeNotFoundException is thrown
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( e.getMessage());
        }
    }
    // Add Showtime with Validation
    @PostMapping
    public ResponseEntity<?> addShowtime(@Valid @RequestBody Showtime showtime,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
        }


        try {
            Showtime addedShowtime = showtimeService.addShowtime(showtime);
            return ResponseEntity.ok(addedShowtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update Showtime with Validation
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long showtimeId, @Valid @RequestBody Showtime showtimeDetails ,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
        }


        try {
            showtimeService.updateShowtime(showtimeId, showtimeDetails);
            return ResponseEntity.ok().build();  // Returns 200 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // Returns 404 Not Found
        }
    }

    // Delete Showtime
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        if (showtimeService.deleteShowtime(showtimeId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}