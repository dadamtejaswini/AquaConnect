package com.aquaconnect.controller;

import com.aquaconnect.dto.AssignBookingRequestDto;
import com.aquaconnect.dto.BookingRequestDto;
import com.aquaconnect.dto.BookingResponseDto;
import com.aquaconnect.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/pending")
    public List<BookingResponseDto> getPendingBookings() {
        return bookingService.getPendingBookings();
    }

    @PutMapping("/{bookingId}/assign")
    public BookingResponseDto assignDriverAndVehicle(
            @PathVariable Long bookingId,
            @RequestBody AssignBookingRequestDto request
    ) {
        return bookingService.assignDriverAndVehicle(bookingId, request);
    }

    @GetMapping("/driver/{driverId}")
    public List<BookingResponseDto> getBookingsByDriver(@PathVariable Long driverId) {
        return bookingService.getBookingsByDriver(driverId);
    }

    @PutMapping("/{bookingId}/out-for-delivery")
    public BookingResponseDto markOutForDelivery(@PathVariable Long bookingId) {
        return bookingService.markOutForDelivery(bookingId);
    }

    @PutMapping("/{bookingId}/delivered")
    public BookingResponseDto markDelivered(@PathVariable Long bookingId) {
        return bookingService.markDelivered(bookingId);
    }

    @GetMapping("/owner/{ownerId}/pending")
    public List<BookingResponseDto> getPendingBookingsByOwner(@PathVariable Long ownerId) {
        return bookingService.getPendingBookingsByOwner(ownerId);
    }
}