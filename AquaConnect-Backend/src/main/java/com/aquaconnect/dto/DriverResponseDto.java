package com.aquaconnect.dto;

import com.aquaconnect.enums.DriverStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponseDto {

    private Long driverId;
    private String driverName;
    private String phoneNumber;
    private Double rating;
    private DriverStatus status;
    private String driverImageUrl;
}