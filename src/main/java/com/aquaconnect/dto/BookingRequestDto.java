package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDto {

    private Long userId;
    private Long branchId;

    private Double quantity;

    private String deliveryAddress;

    private Double deliveryLatitude;

    private Double deliveryLongitude;
}