package com.aquaconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column( nullable = false)
    private String email;

    private String password;

    private String bankAccountNumber;

    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Branch> branches = new ArrayList<>();
}