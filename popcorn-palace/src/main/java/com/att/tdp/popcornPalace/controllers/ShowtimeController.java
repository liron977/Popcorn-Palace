package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.services.ShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long showtimeId) {
        return showtimeService.getShowtimeById(showtimeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addShowtime(@RequestBody Showtime showtime) {
        try {
            Showtime addedShowtime = showtimeService.addShowtime(showtime);
            return ResponseEntity.ok(addedShowtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Void> updateShowtime(@PathVariable Long showtimeId, @RequestBody Showtime showtimeDetails) {
        try {
            showtimeService.updateShowtime(showtimeId, showtimeDetails);
            return ResponseEntity.ok().build();  // Returns 200 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // Returns 404 Not Found
        }
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        if (showtimeService.deleteShowtime(showtimeId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
