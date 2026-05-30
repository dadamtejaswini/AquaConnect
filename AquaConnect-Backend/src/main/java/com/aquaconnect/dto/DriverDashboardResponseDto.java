package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDashboardResponseDto {

    private Long driverId;
    private String driverName;
    private String phoneNumber;
    private String licenseNumber;
    private Double rating;
    private String status;

    private String branchName;

    private String vehicleNumber;
    private Double vehicleCapacity;
}