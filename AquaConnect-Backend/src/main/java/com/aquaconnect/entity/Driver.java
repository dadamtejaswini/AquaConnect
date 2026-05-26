package com.aquaconnect.entity;

import com.aquaconnect.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String driverName;

    private String phoneNumber;

    @Column(unique = true)
    private String licenseNumber;

    private String password;

    private String driverImagePath;

    private String licenseImagePath;

    private Double rating;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @ManyToOne
    private Branch branch;
}