package com.aquaconnect.controller;

import com.aquaconnect.dto.BranchResponseDto;
import com.aquaconnect.dto.NearbyReservoirResponse;
import com.aquaconnect.service.BranchService;
import com.aquaconnect.service.ReservoirService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservoirs")
@RequiredArgsConstructor
public class ReservoirController {

    private final BranchService branchService;
    private final ReservoirService reservoirService;

    @GetMapping
    public List<BranchResponseDto> getAllReservoirs() {
        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public BranchResponseDto getReservoirById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }

    @GetMapping("/nearby")
    public List<NearbyReservoirResponse> getNearbyReservoirs(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "20") Double radius
    ) {
        return reservoirService.getNearbyReservoirs(lat, lng, radius);
    }
}