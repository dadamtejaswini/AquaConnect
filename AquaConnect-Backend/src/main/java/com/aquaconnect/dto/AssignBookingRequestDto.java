package com.aquaconnect.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignBookingRequestDto {

    private Long driverId;
    private Long vehicleId;
}