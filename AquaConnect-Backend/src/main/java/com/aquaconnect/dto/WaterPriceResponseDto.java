package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterPriceResponseDto {

    private Double quantity;
    private Double price;
}