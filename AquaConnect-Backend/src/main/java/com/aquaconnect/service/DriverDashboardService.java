package com.aquaconnect.service;

import com.aquaconnect.dto.BookingResponseDto;
import com.aquaconnect.dto.DriverDashboardResponseDto;
import com.aquaconnect.dto.DriverLocationUpdateRequest;
import com.aquaconnect.entity.Booking;
import com.aquaconnect.entity.Driver;
import com.aquaconnect.entity.Vehicle;
import com.aquaconnect.repository.BookingRepository;
import com.aquaconnect.repository.DriverRepository;
import com.aquaconnect.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverDashboardService {

    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final VehicleRepository vehicleRepository;

    public DriverDashboardResponseDto getDashboard(String licenseNumber) {
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        List<Booking> bookings = bookingRepository.findByDriver(driver);

        Vehicle vehicle = bookings.stream()
                .map(Booking::getVehicle)
                .filter(v -> v != null)
                .findFirst()
                .orElse(null);

        return DriverDashboardResponseDto.builder()
                .driverId(driver.getId())
                .driverName(driver.getDriverName())
                .phoneNumber(driver.getPhoneNumber())
                .licenseNumber(driver.getLicenseNumber())
                .rating(driver.getRating())
                .status(driver.getStatus() != null ? driver.getStatus().name() : null)
                .branchName(driver.getBranch() != null ? driver.getBranch().getBranchName() : null)
                .vehicleNumber(vehicle != null ? vehicle.getVehicleNumber() : null)
                .vehicleCapacity(vehicle != null ? vehicle.getVehicleCapacity() : null)
                .build();
    }

    public List<BookingResponseDto> getMyBookings(String licenseNumber) {
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return bookingRepository.findByDriver(driver)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public String updateVehicleLocation(DriverLocationUpdateRequest request, String licenseNumber) {
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getBranch() == null ||
                driver.getBranch() == null ||
                !vehicle.getBranch().getId().equals(driver.getBranch().getId())) {
            throw new RuntimeException("You cannot update this vehicle location");
        }

        vehicle.setCurrentLatitude(request.getLatitude());
        vehicle.setCurrentLongitude(request.getLongitude());

        vehicleRepository.save(vehicle);

        return "Vehicle location updated successfully";
    }

    public BookingResponseDto markOutForDelivery(Long bookingId, String licenseNumber) {
        validateDriverBooking(bookingId, licenseNumber);
        return bookingService.markOutForDelivery(bookingId);
    }

    public BookingResponseDto markDelivered(Long bookingId, String licenseNumber) {
        validateDriverBooking(bookingId, licenseNumber);
        return bookingService.markDelivered(bookingId);
    }

    private void validateDriverBooking(Long bookingId, String licenseNumber) {
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getDriver() == null ||
                !booking.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You cannot update this booking");
        }
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