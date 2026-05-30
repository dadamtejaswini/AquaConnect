package com.aquaconnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBranchResponseDto {

    private Long branchId;
    private String branchName;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double currentWaterQuantity;
    private String ownerName;
    private List<String> imageUrls;
    private List<WaterPriceRequest> waterPrices;

    private List<VehicleResponseDto> vehicles;
    private List<DriverResponseDto> drivers;
    private List<FeedbackResponseDto> feedbacks;
}