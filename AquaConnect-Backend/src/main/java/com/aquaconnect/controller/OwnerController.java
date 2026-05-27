package com.aquaconnect.controller;

import com.aquaconnect.dto.*;
import com.aquaconnect.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/dashboard")
    public OwnerDashboardResponseDto getOwnerDashboard(Principal principal) {
        return ownerService.getOwnerDashboard(principal.getName());
    }

    @PostMapping("/branches")
    public String addBranch(@RequestBody AddBranchRequest request, Principal principal) {
        return ownerService.addBranch(request, principal.getName());
    }

    @PostMapping("/vehicles")
    public String addVehicle(@RequestBody AddVehicleRequest request, Principal principal) {
        return ownerService.addVehicle(request, principal.getName());
    }

    @PostMapping("/drivers")
    public String addDriver(@RequestBody AddDriverRequest request, Principal principal) {
        return ownerService.addDriver(request, principal.getName());
    }

    @PutMapping("/bookings/{bookingId}/assign")
    public String assignBooking(
            @PathVariable Long bookingId,
            @RequestBody AssignBookingRequest request,
            Principal principal
    ) {
        return ownerService.assignBooking(bookingId, request, principal.getName());
    }

    @PutMapping("/bookings/{bookingId}/status")
    public String updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody UpdateBookingStatusRequest request,
            Principal principal
    ) {
        return ownerService.updateBookingStatus(bookingId, request, principal.getName());
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerOwner(
            @RequestPart("data") String data,
            @RequestPart("reservoirImages") List<MultipartFile> reservoirImages,
            @RequestPart("vehicleImages") List<MultipartFile> vehicleImages,
            @RequestPart("driverImages") List<MultipartFile> driverImages,
            @RequestPart("licenseImages") List<MultipartFile> licenseImages
    ) {
        return ownerService.registerOwner(data, reservoirImages, vehicleImages, driverImages, licenseImages);
    }
}