package com.aquaconnect.service;

import com.aquaconnect.dto.BranchResponseDto;
import com.aquaconnect.entity.Branch;
import com.aquaconnect.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<BranchResponseDto> getAllBranches() {

        return branchRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public BranchResponseDto getBranchById(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        return mapToDto(branch);
    }

    private BranchResponseDto mapToDto(Branch branch) {

        return BranchResponseDto.builder()
                .id(branch.getId())
                .branchName(branch.getBranchName())
                .location(branch.getLocation())
                .latitude(branch.getLatitude())
                .longitude(branch.getLongitude())
                .currentWaterQuantity(branch.getCurrentWaterQuantity())
                .active(branch.getActive())
                .build();
    }
}