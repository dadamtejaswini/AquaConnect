package com.aquaconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branchName;

    private String location;

    private Double latitude;

    private Double longitude;

    private Double currentWaterQuantity;

    private Boolean active;

    @JsonIgnore
    @ManyToOne
    private Owner owner;

    @Builder.Default
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<ReservoirImage> reservoirImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Driver> drivers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<WaterPrice> waterPrices = new ArrayList<>();
}