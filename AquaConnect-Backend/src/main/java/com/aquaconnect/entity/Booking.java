package com.aquaconnect.entity;

import com.aquaconnect.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double quantity;

    private Double totalPrice;

    private String deliveryAddress;

    private Double deliveryLatitude;

    private Double deliveryLongitude;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String ownerNotificationMessage;

    @ManyToOne
    private User user;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Driver driver;

    @ManyToOne
    private Vehicle vehicle;
}