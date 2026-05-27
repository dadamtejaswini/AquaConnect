package com.aquaconnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDashboardResponseDto {

    private Long ownerId;
    private String ownerName;
    private String email;
    private String phoneNumber;

    private int totalBranches;
    private int totalDrivers;
    private int totalVehicles;
    private int totalBookings;
    private Double totalWaterAvailable;

    private List<BranchInfo> branches;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchInfo {
        private Long branchId;
        private String branchName;
        private String location;
        private Double currentWaterQuantity;
        private Boolean active;

        private List<DriverInfo> drivers;
        private List<VehicleInfo> vehicles;
        private List<BookingInfo> bookings;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DriverInfo {
        private Long driverId;
        private String driverName;
        private String phoneNumber;
        private String licenseNumber;
        private Double rating;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehicleInfo {
        private Long vehicleId;
        private String vehicleNumber;
        private Double vehicleCapacity;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingInfo {
        private Long bookingId;
        private String userName;
        private Double quantity;
        private Double totalPrice;
        private String deliveryAddress;
        private String status;
        private String driverName;
        private String vehicleNumber;
    }
}