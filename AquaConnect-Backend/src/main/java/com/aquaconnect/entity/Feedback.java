package com.aquaconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Booking booking;
}