package com.aquaconnect.controller;

import com.aquaconnect.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerOwner(
            @RequestPart("data") String data,
            @RequestPart("reservoirImages") List<MultipartFile> reservoirImages,
            @RequestPart("vehicleImages") List<MultipartFile> vehicleImages,
            @RequestPart("driverImages") List<MultipartFile> driverImages,
            @RequestPart("licenseImages") List<MultipartFile> licenseImages
    ) {
        return ownerService.registerOwner(data, reservoirImages, vehicleImages, driverImages, licenseImages);
    }
}