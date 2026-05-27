package com.aquaconnect.dto;

import lombok.Data;

@Data
public class AddVehicleRequest {
    private Long branchId;
    private String vehicleNumber;
    private Double vehicleCapacity;
}