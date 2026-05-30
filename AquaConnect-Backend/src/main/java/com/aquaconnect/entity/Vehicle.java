package com.aquaconnect.entity;

import com.aquaconnect.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vehicleNumber;

    private Double vehicleCapacity;

    private String vehicleImagePath;

    private Double currentLatitude;

    private Double currentLongitude;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @ManyToOne
    private Branch branch;
}