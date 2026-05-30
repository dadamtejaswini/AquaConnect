package com.aquaconnect.service;

import com.aquaconnect.dto.*;
import com.aquaconnect.entity.Driver;
import com.aquaconnect.entity.Owner;
import com.aquaconnect.entity.User;
import com.aquaconnect.enums.Role;
import com.aquaconnect.exception.InvalidCredentialsException;
import com.aquaconnect.repository.DriverRepository;
import com.aquaconnect.repository.OwnerRepository;
import com.aquaconnect.repository.UserRepository;
import com.aquaconnect.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = Role.valueOf(request.getRole().toUpperCase());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .build();

        userRepository.save(user);

        return "User registered successfully as " + role;
    }

    public AuthResponse login(LoginRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {

            User user = userRepository.findByEmail(request.getEmail()).get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("Invalid credentials");
            }

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name()
            );

            return new AuthResponse(
                    token,
                    user.getRole().name(),
                    "User login successful"
            );
        }

        if (ownerRepository.findByEmail(request.getEmail()).isPresent()) {

            Owner owner = ownerRepository.findByEmail(request.getEmail()).get();

            if (!passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
                throw new InvalidCredentialsException("Invalid owner credentials");
            }

            String token = jwtUtil.generateToken(
                    owner.getEmail(),
                    "OWNER"
            );

            return new AuthResponse(
                    token,
                    "OWNER",
                    "Owner login successful"
            );
        }

        throw new InvalidCredentialsException("Invalid credentials");
    }

    public AuthResponse driverLogin(LoginRequest request) {

        Driver driver = driverRepository.findByLicenseNumber(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid driver credentials"));

        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new InvalidCredentialsException("Invalid driver credentials");
        }

        String token = jwtUtil.generateToken(

                driver.getLicenseNumber(),
                "DRIVER"
        );

        return new AuthResponse(
                token,
                "DRIVER",
                "Driver login successful"
        );
    }

    public String resetPassword(ResetPasswordRequest request) {

        if (request.getRole() == null || request.getIdentifier() == null) {
            throw new RuntimeException("Role and identifier are required");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        if (request.getLastFourDigits() == null || request.getLastFourDigits().length() != 4) {
            throw new RuntimeException("Enter valid last 4 digits of phone number");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        String role = request.getRole().toUpperCase();

        if (role.equals("USER")) {

            User user = userRepository.findByEmail(request.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getPhone().endsWith(request.getLastFourDigits())) {
                throw new RuntimeException("Invalid last 4 digits of phone number");
            }

            user.setPassword(encodedPassword);
            userRepository.save(user);

            return "User password reset successfully";
        }

        if (role.equals("OWNER")) {

            Owner owner = ownerRepository.findByEmail(request.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));

            if (!owner.getPhoneNumber().endsWith(request.getLastFourDigits())) {
                throw new RuntimeException("Invalid last 4 digits of phone number");
            }

            owner.setPassword(encodedPassword);
            ownerRepository.save(owner);

            return "Owner password reset successfully";
        }

        if (role.equals("DRIVER")) {

            Driver driver = driverRepository.findByLicenseNumber(request.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            if (!driver.getPhoneNumber().endsWith(request.getLastFourDigits())) {
                throw new RuntimeException("Invalid last 4 digits of phone number");
            }

            driver.setPassword(encodedPassword);
            driverRepository.save(driver);

            return "Driver password reset successfully";
        }

        throw new RuntimeException("Invalid role");
    }
}