package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaterPriceRequest {

    private Double quantity;
    private Double price;
}