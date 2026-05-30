package com.aquaconnect.controller;

import com.aquaconnect.dto.BookingResponseDto;
import com.aquaconnect.dto.DriverDashboardResponseDto;
import com.aquaconnect.dto.DriverLocationUpdateRequest;
import com.aquaconnect.service.DriverDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverDashboardService driverDashboardService;

    @GetMapping("/dashboard")
    public DriverDashboardResponseDto getDashboard(Principal principal) {
        return driverDashboardService.getDashboard(principal.getName());
    }

    @GetMapping("/bookings")
    public List<BookingResponseDto> getMyBookings(Principal principal) {
        return driverDashboardService.getMyBookings(principal.getName());
    }

    @PutMapping("/bookings/{bookingId}/out-for-delivery")
    public BookingResponseDto markOutForDelivery(
            @PathVariable Long bookingId,
            Principal principal
    ) {
        return driverDashboardService.markOutForDelivery(bookingId, principal.getName());
    }

    @PutMapping("/vehicle-location")
    public String updateVehicleLocation(
            @RequestBody DriverLocationUpdateRequest request,
            Principal principal
    ) {
        return driverDashboardService.updateVehicleLocation(request, principal.getName());
    }

    @PutMapping("/bookings/{bookingId}/delivered")
    public BookingResponseDto markDelivered(
            @PathVariable Long bookingId,
            Principal principal
    ) {
        return driverDashboardService.markDelivered(bookingId, principal.getName());
    }
}