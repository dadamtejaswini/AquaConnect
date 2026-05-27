package com.aquaconnect.repository;

import com.aquaconnect.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    Optional<Driver> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByBranchId(Long branchId);
}