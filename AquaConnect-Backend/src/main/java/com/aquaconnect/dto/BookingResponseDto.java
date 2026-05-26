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
    private String branchName;

    private Double quantity;
    private Double totalPrice;

    private String deliveryAddress;

    private String driverName;
    private String driverPhone;

    private String vehicleNumber;
    private Double vehicleCapacity;

    private BookingStatus status;

    private String ownerNotificationMessage;
}