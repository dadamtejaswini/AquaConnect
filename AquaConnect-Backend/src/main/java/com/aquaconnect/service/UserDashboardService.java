package com.aquaconnect.service;

import com.aquaconnect.dto.*;
import com.aquaconnect.entity.*;
import com.aquaconnect.enums.BookingStatus;
import com.aquaconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final FeedbackRepository feedbackRepository;

    private static final String BASE_URL = "http://localhost:8080/";

    public String getUserName(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getName();
    }

    public List<UserBranchResponseDto> getAvailableBranches(
            String area,
            Double latitude,
            Double longitude
    ) {
        return branchRepository.findAll()
                .stream()
                .filter(branch -> Boolean.TRUE.equals(branch.getActive()))
                .filter(branch ->
                        branch.getCurrentWaterQuantity() != null &&
                                branch.getCurrentWaterQuantity() > 0
                )
                .filter(branch -> matchesAreaOrDistance(branch, area, latitude, longitude))
                .map(this::mapBranchToResponse)
                .toList();
    }

    private boolean matchesAreaOrDistance(
            Branch branch,
            String area,
            Double latitude,
            Double longitude
    ) {
        if (latitude != null &&
                longitude != null &&
                branch.getLatitude() != null &&
                branch.getLongitude() != null) {

            double distance = calculateDistanceInKm(
                    latitude,
                    longitude,
                    branch.getLatitude(),
                    branch.getLongitude()
            );

            return distance <= 10;
        }

        if (area != null && !area.trim().isEmpty()) {
            return branch.getLocation() != null &&
                    branch.getLocation()
                            .toLowerCase()
                            .contains(area.toLowerCase().trim());
        }

        return true;
    }

    private double calculateDistanceInKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {
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

        return earthRadius * c;
    }

    private UserBranchResponseDto mapBranchToResponse(Branch branch) {
        return UserBranchResponseDto.builder()
                .branchId(branch.getId())
                .branchName(branch.getBranchName())
                .location(branch.getLocation())
                .latitude(branch.getLatitude())
                .longitude(branch.getLongitude())
                .currentWaterQuantity(branch.getCurrentWaterQuantity())
                .ownerName(branch.getOwner() != null ? branch.getOwner().getFullName() : null)

                .imageUrls(
                        branch.getReservoirImages()
                                .stream()
                                .map(image -> buildImageUrl(image.getImagePath()))
                                .toList()
                )

                .waterPrices(
                        branch.getWaterPrices()
                                .stream()
                                .map(price -> new WaterPriceRequest(
                                        price.getQuantity(),
                                        price.getPrice()
                                ))
                                .toList()
                )

                .vehicles(
                        branch.getVehicles()
                                .stream()
                                .map(vehicle -> VehicleResponseDto.builder()
                                        .vehicleId(vehicle.getId())
                                        .vehicleNumber(vehicle.getVehicleNumber())
                                        .vehicleCapacity(vehicle.getVehicleCapacity())
                                        .vehicleImageUrl(buildImageUrl(vehicle.getVehicleImagePath()))
                                        .status(vehicle.getStatus())
                                        .build()
                                )
                                .toList()
                )

                .drivers(
                        branch.getDrivers()
                                .stream()
                                .map(driver -> DriverResponseDto.builder()
                                        .driverId(driver.getId())
                                        .driverName(driver.getDriverName())
                                        .phoneNumber(driver.getPhoneNumber())
                                        .rating(driver.getRating())
                                        .status(driver.getStatus())
                                        .driverImageUrl(buildImageUrl(driver.getDriverImagePath()))
                                        .build()
                                )
                                .toList()
                )

                .feedbacks(
                        feedbackRepository.findByBranchIdOrderByCreatedAtDesc(branch.getId())
                                .stream()
                                .map(this::mapFeedbackToResponse)
                                .toList()
                )

                .build();
    }
    private String buildImageUrl(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        String cleanPath = imagePath.replace("\\", "/");

        if (cleanPath.startsWith("http://") || cleanPath.startsWith("https://")) {
            return cleanPath;
        }

        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        return BASE_URL + cleanPath;
    }

    public BookingResponseDto createBooking(BookingRequestDto request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        request.setUserId(user.getId());

        return bookingService.createBooking(request);
    }

    public List<BookingResponseDto> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapBookingToResponse)
                .toList();
    }

    public FeedbackResponseDto addFeedback(FeedbackRequestDto request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getUser() == null ||
                !booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot give feedback for another user's booking");
        }

        if (feedbackRepository.existsByBookingIdAndUserId(booking.getId(), user.getId())) {
            throw new RuntimeException("Feedback already submitted for this booking");
        }

        if (booking.getStatus() != BookingStatus.DELIVERED) {
            throw new RuntimeException("Feedback can be given only after delivery");
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .branch(booking.getBranch())
                .booking(booking)
                .rating(request.getRating())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        return mapFeedbackToResponse(feedbackRepository.save(feedback));
    }

    public List<FeedbackResponseDto> getMyFeedbacks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapFeedbackToResponse)
                .toList();
    }

    private BookingResponseDto mapBookingToResponse(Booking booking) {
        Driver driver = booking.getDriver();
        Vehicle vehicle = booking.getVehicle();

        boolean feedbackGiven = false;

        if (booking.getUser() != null) {
            feedbackGiven = feedbackRepository.existsByBookingIdAndUserId(
                    booking.getId(),
                    booking.getUser().getId()
            );
        }

        return BookingResponseDto.builder()
                .bookingId(booking.getId())
                .userName(booking.getUser() != null ? booking.getUser().getName() : "N/A")
                .branchName(booking.getBranch() != null ? booking.getBranch().getBranchName() : "N/A")
                .quantity(booking.getQuantity())
                .totalPrice(booking.getTotalPrice())
                .deliveryAddress(booking.getDeliveryAddress())
                .deliveryLatitude(booking.getDeliveryLatitude())
                .deliveryLongitude(booking.getDeliveryLongitude())
                .driverName(driver != null ? driver.getDriverName() : null)
                .driverPhone(driver != null ? driver.getPhoneNumber() : null)
                .vehicleNumber(vehicle != null ? vehicle.getVehicleNumber() : null)
                .vehicleCapacity(vehicle != null ? vehicle.getVehicleCapacity() : null)
                .vehicleCurrentLatitude(vehicle != null ? vehicle.getCurrentLatitude() : null)
                .vehicleCurrentLongitude(vehicle != null ? vehicle.getCurrentLongitude() : null)
                .status(booking.getStatus())
                .ownerNotificationMessage(booking.getOwnerNotificationMessage())
                .feedbackGiven(feedbackGiven)
                .build();
    }

    private FeedbackResponseDto mapFeedbackToResponse(Feedback feedback) {
        return FeedbackResponseDto.builder()
                .feedbackId(feedback.getId())
                .userName(feedback.getUser() != null ? feedback.getUser().getName() : "N/A")
                .branchName(feedback.getBranch() != null ? feedback.getBranch().getBranchName() : "N/A")
                .bookingId(feedback.getBooking() != null ? feedback.getBooking().getId() : null)
                .rating(feedback.getRating())
                .message(feedback.getMessage())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}