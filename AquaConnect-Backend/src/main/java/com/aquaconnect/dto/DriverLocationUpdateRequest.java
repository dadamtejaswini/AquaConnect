package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationUpdateRequest {
    private Long vehicleId;
    private Double latitude;
    private Double longitude;
}