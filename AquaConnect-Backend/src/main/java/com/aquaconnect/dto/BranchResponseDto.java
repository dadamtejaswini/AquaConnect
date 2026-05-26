package com.aquaconnect.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponseDto {

    private Long id;

    private String branchName;

    private String location;

    private Double latitude;

    private Double longitude;

    private Double currentWaterQuantity;

    private Boolean active;
}