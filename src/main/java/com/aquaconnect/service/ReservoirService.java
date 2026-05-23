package com.aquaconnect.service;

import com.aquaconnect.dto.NearbyReservoirResponse;
import com.aquaconnect.entity.Branch;
import com.aquaconnect.entity.WaterPrice;
import com.aquaconnect.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservoirService {

    private final BranchRepository branchRepository;

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
                                            : branch.getReservoirImages().get(0).getImagePath()
                            )
                            .availableVehicles(branch.getVehicles().size())
                            .availableDrivers(branch.getDrivers().size())
                            .startingPrice(
                                    branch.getWaterPrices().stream()
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

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {

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