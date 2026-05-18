package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequest {

    private String driverName;
    private String phoneNumber;
    private String licenseNumber;
}