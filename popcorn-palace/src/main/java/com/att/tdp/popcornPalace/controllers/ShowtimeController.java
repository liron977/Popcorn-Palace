package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.services.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long showtimeId) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        return ResponseEntity.ok(showtime);
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@Valid @RequestBody Showtime showtime) {
        Showtime addedShowtime = showtimeService.addShowtime(showtime);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedShowtime);
    }
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Void> updateShowtime(
            @PathVariable Long showtimeId,
            @Valid @RequestBody Showtime showtimeDetails) {

        showtimeService.updateShowtime(showtimeId, showtimeDetails);
        return ResponseEntity.ok().build();
    }

    // Delete Showtime
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.noContent().build();
    }
}