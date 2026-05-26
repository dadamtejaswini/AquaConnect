package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearbyReservoirResponse {

    private Long branchId;
    private String branchName;
    private String location;

    private Double latitude;
    private Double longitude;

    private Double currentWaterQuantity;
    private Double distanceInKm;

    private String reservoirImage;

    private Integer availableVehicles;
    private Integer availableDrivers;

    private Double startingPrice;
}