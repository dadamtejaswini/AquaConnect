package com.aquaconnect.repository;

import com.aquaconnect.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByBranchId(Long branchId);
}