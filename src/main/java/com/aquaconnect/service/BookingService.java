package com.aquaconnect.service;

import com.aquaconnect.dto.BookingRequestDto;
import com.aquaconnect.dto.BookingResponseDto;
import com.aquaconnect.entity.*;
import com.aquaconnect.enums.BookingStatus;
import com.aquaconnect.enums.DriverStatus;
import com.aquaconnect.enums.VehicleStatus;
import com.aquaconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public BookingResponseDto createBooking(BookingRequestDto request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Reservoir branch not found"));

        if (branch.getCurrentWaterQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough water available in this branch");
        }

        Vehicle vehicle = branch.getVehicles().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .filter(v -> v.getVehicleCapacity() >= request.getQuantity())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available vehicle for this quantity"));

        Driver driver = branch.getDrivers().stream()
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available driver"));

        Double totalPrice = branch.getWaterPrices().stream()
                .filter(price -> price.getQuantity().equals(request.getQuantity()))
                .map(WaterPrice::getPrice)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Price not found for selected quantity"));

        branch.setCurrentWaterQuantity(
                branch.getCurrentWaterQuantity() - request.getQuantity()
        );

        vehicle.setStatus(VehicleStatus.ON_DELIVERY);
        driver.setStatus(DriverStatus.ON_DELIVERY);

        String ownerMessage =
                "New booking received for " + branch.getBranchName()
                        + ". Customer: " + user.getName()
                        + ", Quantity: " + request.getQuantity() + " liters"
                        + ", Driver: " + driver.getDriverName()
                        + ", Vehicle: " + vehicle.getVehicleNumber();

        Booking booking = Booking.builder()
                .user(user)
                .branch(branch)
                .driver(driver)
                .vehicle(vehicle)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLatitude(request.getDeliveryLatitude())
                .deliveryLongitude(request.getDeliveryLongitude())
                .bookingTime(LocalDateTime.now())
                .status(BookingStatus.DRIVER_ASSIGNED)
                .ownerNotificationMessage(ownerMessage)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingResponseDto.builder()
                .bookingId(savedBooking.getId())
                .userName(user.getName())
                .branchName(branch.getBranchName())
                .quantity(savedBooking.getQuantity())
                .totalPrice(savedBooking.getTotalPrice())
                .deliveryAddress(savedBooking.getDeliveryAddress())
                .driverName(driver.getDriverName())
                .driverPhone(driver.getPhoneNumber())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleCapacity(vehicle.getVehicleCapacity())
                .status(savedBooking.getStatus())
                .ownerNotificationMessage(ownerMessage)
                .build();
    }
}