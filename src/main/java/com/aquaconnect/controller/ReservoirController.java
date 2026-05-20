package com.aquaconnect.controller;

import com.aquaconnect.dto.BranchResponseDto;
import com.aquaconnect.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservoirs")
@RequiredArgsConstructor
public class ReservoirController {

    private final BranchService branchService;

    @GetMapping
    public List<BranchResponseDto> getAllReservoirs() {
        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public BranchResponseDto getReservoirById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }
}