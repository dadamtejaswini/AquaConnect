package com.aquaconnect.service;

import com.aquaconnect.dto.OwnerRegisterRequest;
import com.aquaconnect.entity.*;
import com.aquaconnect.enums.DriverStatus;
import com.aquaconnect.enums.VehicleStatus;
import com.aquaconnect.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    public String registerOwner(
            String data,
            List<MultipartFile> reservoirImages,
            List<MultipartFile> vehicleImages,
            List<MultipartFile> driverImages,
            List<MultipartFile> licenseImages
    ) {
        try {
            OwnerRegisterRequest request = objectMapper.readValue(data, OwnerRegisterRequest.class);

            if (ownerRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            if (ownerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already exists");
            }

            if (reservoirImages == null || reservoirImages.size() < 3) {
                throw new RuntimeException("Minimum 3 reservoir images required");
            }

            if (request.getVehicles().size() != vehicleImages.size()) {
                throw new RuntimeException("Vehicle details and vehicle images count must match");
            }

            if (request.getDrivers().size() != driverImages.size()) {
                throw new RuntimeException("Driver details and driver images count must match");
            }

            if (request.getDrivers().size() != licenseImages.size()) {
                throw new RuntimeException("Driver details and license images count must match");
            }

            Owner owner = Owner.builder()
                    .fullName(request.getFullName())
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .bankAccountNumber(request.getBankAccountNumber())
                    .createdAt(LocalDateTime.now())
                    .build();

            Branch branch = Branch.builder()
                    .branchName(request.getBranchName())
                    .location(request.getLocation())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .currentWaterQuantity(request.getCurrentWaterQuantity())
                    .active(true)
                    .owner(owner)
                    .build();

            for (MultipartFile image : reservoirImages) {
                ReservoirImage reservoirImage = ReservoirImage.builder()
                        .imagePath(fileStorageService.saveFile(image, "reservoirs"))
                        .branch(branch)
                        .build();

                branch.getReservoirImages().add(reservoirImage);
            }

            for (int i = 0; i < request.getVehicles().size(); i++) {
                Vehicle vehicle = Vehicle.builder()
                        .vehicleNumber(request.getVehicles().get(i).getVehicleNumber())
                        .vehicleCapacity(request.getVehicles().get(i).getVehicleCapacity())
                        .vehicleImagePath(fileStorageService.saveFile(vehicleImages.get(i), "vehicles"))
                        .status(VehicleStatus.AVAILABLE)
                        .branch(branch)
                        .build();

                branch.getVehicles().add(vehicle);
            }

            for (int i = 0; i < request.getDrivers().size(); i++) {
                Driver driver = Driver.builder()
                        .driverName(request.getDrivers().get(i).getDriverName())
                        .phoneNumber(request.getDrivers().get(i).getPhoneNumber())
                        .licenseNumber(request.getDrivers().get(i).getLicenseNumber())
                        .password(passwordEncoder.encode(request.getDrivers().get(i).getPhoneNumber()))
                        .driverImagePath(fileStorageService.saveFile(driverImages.get(i), "drivers"))
                        .licenseImagePath(fileStorageService.saveFile(licenseImages.get(i), "licenses"))
                        .rating(5.0)
                        .status(DriverStatus.AVAILABLE)
                        .branch(branch)
                        .build();

                branch.getDrivers().add(driver);
            }

            request.getWaterPrices().forEach(priceRequest -> {
                WaterPrice waterPrice = WaterPrice.builder()
                        .quantity(priceRequest.getQuantity())
                        .price(priceRequest.getPrice())
                        .branch(branch)
                        .build();

                branch.getWaterPrices().add(waterPrice);
            });

            owner.getBranches().add(branch);

            ownerRepository.save(owner);

            return "Owner and first branch registered successfully";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}