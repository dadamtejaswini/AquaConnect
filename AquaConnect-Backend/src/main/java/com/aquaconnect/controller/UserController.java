package com.aquaconnect.controller;

import com.aquaconnect.dto.*;
import com.aquaconnect.service.UserDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserDashboardService userDashboardService;

    @GetMapping("/name")
    public String getUserName(Principal principal) {
        return userDashboardService.getUserName(principal.getName());
    }

    @GetMapping("/branches")
    public List<UserBranchResponseDto> getAvailableBranches(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        return userDashboardService.getAvailableBranches(area, latitude, longitude);
    }

    @PostMapping("/bookings")
    public BookingResponseDto createBooking(
            @RequestBody BookingRequestDto request,
            Principal principal
    ) {
        return userDashboardService.createBooking(request, principal.getName());
    }

    @GetMapping("/bookings")
    public List<BookingResponseDto> getMyBookings(Principal principal) {
        return userDashboardService.getMyBookings(principal.getName());
    }

    @PostMapping("/feedbacks")
    public FeedbackResponseDto addFeedback(
            @RequestBody FeedbackRequestDto request,
            Principal principal
    ) {
        return userDashboardService.addFeedback(request, principal.getName());
    }

    @GetMapping("/feedbacks")
    public List<FeedbackResponseDto> getMyFeedbacks(Principal principal) {
        return userDashboardService.getMyFeedbacks(principal.getName());
    }
}