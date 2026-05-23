package com.aquaconnect.controller;

import com.aquaconnect.dto.BookingRequestDto;
import com.aquaconnect.dto.BookingResponseDto;
import com.aquaconnect.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto request) {
        return bookingService.createBooking(request);
    }
}