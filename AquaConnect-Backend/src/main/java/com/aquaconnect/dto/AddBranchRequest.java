package com.aquaconnect.dto;

import lombok.Data;

@Data
public class AddBranchRequest {
    private String branchName;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double currentWaterQuantity;
}