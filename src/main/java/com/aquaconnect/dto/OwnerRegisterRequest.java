package com.aquaconnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRegisterRequest {

    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private String bankAccountNumber;

    private String branchName;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double currentWaterQuantity;

    private List<VehicleRequest> vehicles;
    private List<DriverRequest> drivers;
    private List<WaterPriceRequest> waterPrices;
}