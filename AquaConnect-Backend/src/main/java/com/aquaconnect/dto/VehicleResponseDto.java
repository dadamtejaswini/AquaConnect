package com.aquaconnect.dto;

import com.aquaconnect.enums.VehicleStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {

    private Long vehicleId;
    private String vehicleNumber;
    private Double vehicleCapacity;
    private String vehicleImageUrl;
    private VehicleStatus status;
}