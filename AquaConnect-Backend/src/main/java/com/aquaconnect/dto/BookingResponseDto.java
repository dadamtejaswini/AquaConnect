package com.aquaconnect.dto;

import com.aquaconnect.enums.BookingStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {

    private Long bookingId;

    private String userName;
    private String userPhone;
    private String branchName;

    private Double quantity;
    private Double totalPrice;

    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;

    private String driverName;
    private String driverPhone;

    private Boolean feedbackGiven;

    private Long vehicleId;
    private String vehicleNumber;
    private Double vehicleCapacity;
    private Double vehicleCurrentLatitude;
    private Double vehicleCurrentLongitude;

    private Double distanceKm;

    private BookingStatus status;

    private String ownerNotificationMessage;
}