package com.aquaconnect.service;

import com.aquaconnect.dto.*;
import com.aquaconnect.entity.*;
import com.aquaconnect.repository.BranchRepository;
import com.aquaconnect.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservoirService {

    private final BranchRepository branchRepository;
    private final FeedbackRepository feedbackRepository;

    private static final String BASE_URL = "http://localhost:8080/";

    public List<NearbyReservoirResponse> getNearbyReservoirs(
            Double userLat,
            Double userLng,
            Double radius
    ) {

        List<Branch> branches = branchRepository.findAll();

        return branches.stream()
                .filter(branch -> Boolean.TRUE.equals(branch.getActive()))
                .map(branch -> {
                    double distance = calculateDistance(
                            userLat,
                            userLng,
                            branch.getLatitude(),
                            branch.getLongitude()
                    );

                    return NearbyReservoirResponse.builder()
                            .branchId(branch.getId())
                            .branchName(branch.getBranchName())
                            .location(branch.getLocation())
                            .latitude(branch.getLatitude())
                            .longitude(branch.getLongitude())
                            .currentWaterQuantity(branch.getCurrentWaterQuantity())
                            .distanceInKm(distance)

                            .reservoirImage(
                                    branch.getReservoirImages().isEmpty()
                                            ? null
                                            : buildImageUrl(branch.getReservoirImages().get(0).getImagePath())
                            )

                            .imageUrls(
                                    branch.getReservoirImages()
                                            .stream()
                                            .map(image -> buildImageUrl(image.getImagePath()))
                                            .toList()
                            )

                            .availableVehicles(branch.getVehicles().size())
                            .availableDrivers(branch.getDrivers().size())

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

                            .startingPrice(
                                    branch.getWaterPrices()
                                            .stream()
                                            .map(WaterPrice::getPrice)
                                            .min(Double::compareTo)
                                            .orElse(0.0)
                            )
                            .build();
                })
                .filter(response -> response.getDistanceInKm() <= radius)
                .sorted(Comparator.comparing(NearbyReservoirResponse::getDistanceInKm))
                .toList();
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

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {

        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 0.0;
        }

        final int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round((EARTH_RADIUS * c) * 100.0) / 100.0;
    }
}