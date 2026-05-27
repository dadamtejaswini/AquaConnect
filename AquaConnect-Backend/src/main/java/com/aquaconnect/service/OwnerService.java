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
import com.aquaconnect.dto.OwnerDashboardResponseDto;
import com.aquaconnect.repository.BookingRepository;
import com.aquaconnect.dto.*;
import com.aquaconnect.enums.BookingStatus;
import com.aquaconnect.repository.BranchRepository;
import com.aquaconnect.repository.DriverRepository;
import com.aquaconnect.repository.VehicleRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final BookingRepository bookingRepository;
    private final BranchRepository branchRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public String addBranch(AddBranchRequest request, String ownerEmail) {

        Owner owner = ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Branch branch = Branch.builder()
                .branchName(request.getBranchName())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .currentWaterQuantity(request.getCurrentWaterQuantity())
                .active(true)
                .owner(owner)
                .build();

        owner.getBranches().add(branch);
        ownerRepository.save(owner);

        return "Branch added successfully";
    }

    public String addVehicle(AddVehicleRequest request, String ownerEmail) {

        Owner owner = ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        if (!branch.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot add vehicle to another owner's branch");
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.getVehicleNumber())
                .vehicleCapacity(request.getVehicleCapacity())
                .status(VehicleStatus.AVAILABLE)
                .branch(branch)
                .build();

        vehicleRepository.save(vehicle);

        return "Vehicle added successfully";
    }

    public String addDriver(AddDriverRequest request, String ownerEmail) {

        Owner owner = ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        if (!branch.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot add driver to another owner's branch");
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists");
        }

        Driver driver = Driver.builder()
                .driverName(request.getDriverName())
                .phoneNumber(request.getPhoneNumber())
                .licenseNumber(request.getLicenseNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .rating(5.0)
                .status(DriverStatus.AVAILABLE)
                .branch(branch)
                .build();

        driverRepository.save(driver);

        return "Driver added successfully";
    }

    public String assignBooking(Long bookingId, AssignBookingRequest request, String ownerEmail) {

        Owner owner = ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getBranch() == null || !booking.getBranch().getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot assign this booking");
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (!driver.getBranch().getId().equals(booking.getBranch().getId())) {
            throw new RuntimeException("Driver does not belong to this branch");
        }

        if (!vehicle.getBranch().getId().equals(booking.getBranch().getId())) {
            throw new RuntimeException("Vehicle does not belong to this branch");
        }

        booking.setDriver(driver);
        booking.setVehicle(vehicle);
        booking.setStatus(BookingStatus.DRIVER_ASSIGNED);

        driver.setStatus(DriverStatus.ON_DELIVERY);
        vehicle.setStatus(VehicleStatus.ON_DELIVERY);

        bookingRepository.save(booking);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        return "Driver and vehicle assigned successfully";
    }

    public String updateBookingStatus(Long bookingId, UpdateBookingStatusRequest request, String ownerEmail) {

        Owner owner = ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getBranch() == null || !booking.getBranch().getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot update this booking");
        }

        BookingStatus status = BookingStatus.valueOf(request.getStatus().toUpperCase());
        booking.setStatus(status);

        if (status == BookingStatus.DELIVERED || status == BookingStatus.CANCELLED) {
            if (booking.getDriver() != null) {
                booking.getDriver().setStatus(DriverStatus.AVAILABLE);
                driverRepository.save(booking.getDriver());
            }

            if (booking.getVehicle() != null) {
                booking.getVehicle().setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(booking.getVehicle());
            }
        }

        bookingRepository.save(booking);

        return "Booking status updated successfully";
    }

    public OwnerDashboardResponseDto getOwnerDashboard(String email) {

        Owner owner = ownerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        List<OwnerDashboardResponseDto.BranchInfo> branchInfos =
                owner.getBranches().stream().map(branch -> {

                    List<Booking> bookings = bookingRepository.findAll()
                            .stream()
                            .filter(booking -> booking.getBranch() != null
                                    && booking.getBranch().getId().equals(branch.getId()))
                            .toList();

                    return OwnerDashboardResponseDto.BranchInfo.builder()
                            .branchId(branch.getId())
                            .branchName(branch.getBranchName())
                            .location(branch.getLocation())
                            .currentWaterQuantity(branch.getCurrentWaterQuantity())
                            .active(branch.getActive())

                            .drivers(
                                    branch.getDrivers().stream()
                                            .map(driver -> OwnerDashboardResponseDto.DriverInfo.builder()
                                                    .driverId(driver.getId())
                                                    .driverName(driver.getDriverName())
                                                    .phoneNumber(driver.getPhoneNumber())
                                                    .licenseNumber(driver.getLicenseNumber())
                                                    .rating(driver.getRating())
                                                    .status(driver.getStatus().name())
                                                    .build())
                                            .toList()
                            )

                            .vehicles(
                                    branch.getVehicles().stream()
                                            .map(vehicle -> OwnerDashboardResponseDto.VehicleInfo.builder()
                                                    .vehicleId(vehicle.getId())
                                                    .vehicleNumber(vehicle.getVehicleNumber())
                                                    .vehicleCapacity(vehicle.getVehicleCapacity())
                                                    .status(vehicle.getStatus().name())
                                                    .build())
                                            .toList()
                            )

                            .bookings(
                                    bookings.stream()
                                            .map(booking -> OwnerDashboardResponseDto.BookingInfo.builder()
                                                    .bookingId(booking.getId())
                                                    .userName(booking.getUser() != null ? booking.getUser().getName() : "N/A")
                                                    .quantity(booking.getQuantity())
                                                    .totalPrice(booking.getTotalPrice())
                                                    .deliveryAddress(booking.getDeliveryAddress())
                                                    .status(booking.getStatus().name())
                                                    .driverName(booking.getDriver() != null ? booking.getDriver().getDriverName() : "Not Assigned")
                                                    .vehicleNumber(booking.getVehicle() != null ? booking.getVehicle().getVehicleNumber() : "Not Assigned")
                                                    .build())
                                            .toList()
                            )

                            .build();
                }).toList();

        int totalDrivers = owner.getBranches()
                .stream()
                .mapToInt(branch -> branch.getDrivers().size())
                .sum();

        int totalVehicles = owner.getBranches()
                .stream()
                .mapToInt(branch -> branch.getVehicles().size())
                .sum();

        int totalBookings = branchInfos
                .stream()
                .mapToInt(branch -> branch.getBookings().size())
                .sum();

        Double totalWater = owner.getBranches()
                .stream()
                .mapToDouble(branch -> branch.getCurrentWaterQuantity() == null ? 0 : branch.getCurrentWaterQuantity())
                .sum();

        return OwnerDashboardResponseDto.builder()
                .ownerId(owner.getId())
                .ownerName(owner.getFullName())
                .email(owner.getEmail())
                .phoneNumber(owner.getPhoneNumber())
                .totalBranches(owner.getBranches().size())
                .totalDrivers(totalDrivers)
                .totalVehicles(totalVehicles)
                .totalBookings(totalBookings)
                .totalWaterAvailable(totalWater)
                .branches(branchInfos)
                .build();
    }

    public String registerOwner(
            String data,
            List<MultipartFile> reservoirImages,
            List<MultipartFile> vehicleImages,
            List<MultipartFile> driverImages,
            List<MultipartFile> licenseImages
    ) {
        try {
            OwnerRegisterRequest request =
                    objectMapper.readValue(data, OwnerRegisterRequest.class);

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

            Owner owner = ownerRepository.findByEmail(request.getEmail())
                    .orElse(null);

            boolean newOwner = false;

            if (owner == null) {
                owner = Owner.builder()
                        .fullName(request.getFullName())
                        .phoneNumber(request.getPhoneNumber())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .bankAccountNumber(request.getBankAccountNumber())
                        .createdAt(LocalDateTime.now())
                        .build();

                newOwner = true;
            }

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

                String driverName = request.getDrivers().get(i).getDriverName();
                String phoneNumber = request.getDrivers().get(i).getPhoneNumber();
                String licenseNumber = request.getDrivers().get(i).getLicenseNumber();

                String rawPassword = generateDriverPassword(driverName, licenseNumber);

                Driver driver = Driver.builder()
                        .driverName(driverName)
                        .phoneNumber(phoneNumber)
                        .licenseNumber(licenseNumber)
                        .password(passwordEncoder.encode(rawPassword))
                        .driverImagePath(fileStorageService.saveFile(driverImages.get(i), "drivers"))
                        .licenseImagePath(fileStorageService.saveFile(licenseImages.get(i), "licenses"))
                        .rating(5.0)
                        .status(DriverStatus.AVAILABLE)
                        .branch(branch)
                        .build();

                branch.getDrivers().add(driver);

                System.out.println("==================================");
                System.out.println("Driver Created Successfully");
                System.out.println("Name      : " + driverName);
                System.out.println("Login ID  : " + phoneNumber);
                System.out.println("Password  : " + rawPassword);
                System.out.println("==================================");
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

            if (newOwner) {
                return "Owner and first branch registered successfully";
            }

            return "New branch added successfully for existing owner";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateDriverPassword(String driverName, String licenseNumber) {

        String firstName = driverName.trim().split(" ")[0].toLowerCase();

        String digits = licenseNumber.replaceAll("[^0-9]", "");

        String lastSix = digits.length() >= 6
                ? digits.substring(digits.length() - 6)
                : digits;

        return firstName + lastSix;
    }
}