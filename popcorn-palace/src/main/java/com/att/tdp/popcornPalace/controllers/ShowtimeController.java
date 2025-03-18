package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeRepository showtimeRepository;

    public ShowtimeController(ShowtimeRepository showtimeRepository){
        this.showtimeRepository= showtimeRepository;
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Showtime addShowtime(@RequestBody Showtime showtime) {
        return showtimeRepository.save(showtime);
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        if (showtimeRepository.existsById(showtimeId)) {
            showtimeRepository.deleteById(showtimeId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


}