package com.aquaconnect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file, String folder) {
        try {
            Path folderPath = Paths.get(uploadDir, folder);
            Files.createDirectories(folderPath);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + folder + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }
    }
}