package com.aquaconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "water_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double quantity;

    private Double price;

    @ManyToOne
    private Branch branch;
}