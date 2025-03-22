package com.att.tdp.popcornPalace.controllers;

import com.att.tdp.popcornPalace.dto.BookingRequestDto;
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
    public ResponseEntity<ApiResponse<Map<String, String>>> bookTicket(@Valid @RequestBody BookingRequestDto bookingRequestDto) {
        String bookingId = bookingService.bookTicket(bookingRequestDto.getShowtimeId(),bookingRequestDto.getSeatNumber(),bookingRequestDto.getUserId() );

        // Prepare the response
        Map<String, String> response = new HashMap<>();
        response.put("bookingId", bookingId);

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

}