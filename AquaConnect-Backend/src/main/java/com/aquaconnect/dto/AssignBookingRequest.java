package com.aquaconnect.dto;

import lombok.Data;

@Data
public class AssignBookingRequest {
    private Long driverId;
    private Long vehicleId;
}