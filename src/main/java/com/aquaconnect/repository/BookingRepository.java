package com.aquaconnect.repository;

import com.aquaconnect.entity.Booking;
import com.aquaconnect.entity.Driver;
import com.aquaconnect.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByDriver(Driver driver);

    List<Booking> findByBranchOwnerIdAndStatus(Long ownerId, BookingStatus status);
}