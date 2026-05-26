package com.aquaconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservoir_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservoirImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @ManyToOne
    private Branch branch;
}