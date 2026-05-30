package com.aquaconnect.service;

import com.aquaconnect.dto.AssignBookingRequestDto;
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
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public BookingResponseDto createBooking(BookingRequestDto request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Reservoir branch not found"));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        if (branch.getCurrentWaterQuantity() == null ||
                branch.getCurrentWaterQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough water available in this branch");
        }

        if (branch.getWaterPrices() == null || branch.getWaterPrices().isEmpty()) {
            throw new RuntimeException("Price not added for this branch");
        }

        Double totalPrice = branch.getWaterPrices()
                .stream()
                .filter(price -> price.getQuantity().equals(request.getQuantity()))
                .map(WaterPrice::getPrice)
                .findFirst()
                .orElseGet(() -> {
                    WaterPrice basePrice = branch.getWaterPrices()
                            .stream()
                            .max(Comparator.comparing(WaterPrice::getQuantity))
                            .orElseThrow(() -> new RuntimeException("Price not found"));

                    double pricePerLiter = basePrice.getPrice() / basePrice.getQuantity();
                    return request.getQuantity() * pricePerLiter;
                });

        branch.setCurrentWaterQuantity(branch.getCurrentWaterQuantity() - request.getQuantity());
        branchRepository.save(branch);

        String ownerMessage =
                "New booking received for " + branch.getBranchName()
                        + ". Customer: " + user.getName()
                        + ", Quantity: " + request.getQuantity() + " liters"
                        + ". Please assign driver and vehicle.";

        Booking booking = Booking.builder()
                .user(user)
                .branch(branch)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLatitude(request.getDeliveryLatitude())
                .deliveryLongitude(request.getDeliveryLongitude())
                .bookingTime(LocalDateTime.now())
                .status(BookingStatus.PENDING)
                .ownerNotificationMessage(ownerMessage)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    public List<BookingResponseDto> getPendingBookings() {
        return bookingRepository.findByStatus(BookingStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BookingResponseDto> getPendingBookingsByOwner(Long ownerId) {
        return bookingRepository.findByBranchOwnerIdAndStatus(ownerId, BookingStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BookingResponseDto assignDriverAndVehicle(Long bookingId, AssignBookingRequestDto request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be assigned");
        }

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available");
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available");
        }

        if (vehicle.getVehicleCapacity() < booking.getQuantity()) {
            throw new RuntimeException("Vehicle capacity is not enough for this booking");
        }

        booking.setDriver(driver);
        booking.setVehicle(vehicle);
        booking.setStatus(BookingStatus.DRIVER_ASSIGNED);

        booking.setOwnerNotificationMessage(
                "Booking assigned successfully. Driver " + driver.getDriverName()
                        + " (" + driver.getPhoneNumber() + ")"
                        + " has been assigned with vehicle " + vehicle.getVehicleNumber()
                        + ". Status updated to DRIVER_ASSIGNED."
        );

        driver.setStatus(DriverStatus.ON_DELIVERY);
        vehicle.setStatus(VehicleStatus.ON_DELIVERY);

        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    public List<BookingResponseDto> getBookingsByDriver(Long driverId) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return bookingRepository.findByDriver(driver)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BookingResponseDto markOutForDelivery(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.DRIVER_ASSIGNED) {
            throw new RuntimeException("Only assigned bookings can be marked as out for delivery");
        }

        booking.setStatus(BookingStatus.OUT_FOR_DELIVERY);

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    public BookingResponseDto markDelivered(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Only out for delivery bookings can be marked as delivered");
        }

        booking.setStatus(BookingStatus.DELIVERED);

        Driver driver = booking.getDriver();
        Vehicle vehicle = booking.getVehicle();

        if (driver != null) {
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }

        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    private BookingResponseDto mapToResponse(Booking booking) {

        Driver driver = booking.getDriver();
        Vehicle vehicle = booking.getVehicle();

        Double distanceKm = null;

        if (vehicle != null &&
                vehicle.getCurrentLatitude() != null &&
                vehicle.getCurrentLongitude() != null &&
                booking.getDeliveryLatitude() != null &&
                booking.getDeliveryLongitude() != null) {

            distanceKm = calculateDistanceInKm(
                    vehicle.getCurrentLatitude(),
                    vehicle.getCurrentLongitude(),
                    booking.getDeliveryLatitude(),
                    booking.getDeliveryLongitude()
            );
        }

        return BookingResponseDto.builder()
                .bookingId(booking.getId())
                .userName(booking.getUser() != null ? booking.getUser().getName() : null)
                .userPhone(booking.getUser() != null ? booking.getUser().getPhone() : null)
                .branchName(booking.getBranch() != null ? booking.getBranch().getBranchName() : null)
                .quantity(booking.getQuantity())
                .totalPrice(booking.getTotalPrice())
                .deliveryAddress(booking.getDeliveryAddress())
                .deliveryLatitude(booking.getDeliveryLatitude())
                .deliveryLongitude(booking.getDeliveryLongitude())
                .driverName(driver != null ? driver.getDriverName() : null)
                .driverPhone(driver != null ? driver.getPhoneNumber() : null)
                .vehicleId(vehicle != null ? vehicle.getId() : null)
                .vehicleNumber(vehicle != null ? vehicle.getVehicleNumber() : null)
                .vehicleCapacity(vehicle != null ? vehicle.getVehicleCapacity() : null)
                .vehicleCurrentLatitude(vehicle != null ? vehicle.getCurrentLatitude() : null)
                .vehicleCurrentLongitude(vehicle != null ? vehicle.getCurrentLongitude() : null)
                .distanceKm(distanceKm)
                .status(booking.getStatus())
                .ownerNotificationMessage(booking.getOwnerNotificationMessage())
                .build();
    }

    private double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final int earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) *
                                Math.cos(Math.toRadians(lat2)) *
                                Math.sin(dLon / 2) *
                                Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round((earthRadius * c) * 100.0) / 100.0;
    }
}