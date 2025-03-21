package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.dto.ShowtimeRequestDto;
import com.att.tdp.popcornPalace.dto.ShowtimeResponseDto;
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
    public ResponseEntity<ShowtimeResponseDto> getShowtimeById(@PathVariable Long showtimeId) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        ShowtimeResponseDto responseDto = showtimeService.mapToShowtimeResponseDto(showtime);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<ShowtimeResponseDto> addShowtime(@Valid @RequestBody ShowtimeRequestDto showtime) {
        Showtime addedShowtime = showtimeService.addShowtime(showtime);
        ShowtimeResponseDto responseDto = showtimeService.mapToShowtimeResponseDto(addedShowtime);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Void> updateShowtime(
            @PathVariable Long showtimeId,
            @Valid @RequestBody ShowtimeRequestDto showtimeDetails) {

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