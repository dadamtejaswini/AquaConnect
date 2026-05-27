package com.aquaconnect.dto;

import lombok.Data;

@Data
public class AddDriverRequest {
    private Long branchId;
    private String driverName;
    private String phoneNumber;
    private String licenseNumber;
    private String password;
}