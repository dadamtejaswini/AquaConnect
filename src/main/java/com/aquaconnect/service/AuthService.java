package com.aquaconnect.service;

import com.aquaconnect.dto.*;
import com.aquaconnect.entity.User;
import com.aquaconnect.enums.Role;
import com.aquaconnect.exception.InvalidCredentialsException;
import com.aquaconnect.repository.UserRepository;
import com.aquaconnect.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.aquaconnect.entity.Driver;
import com.aquaconnect.repository.DriverRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final DriverRepository driverRepository;

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

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getRole().name(),
                "Login successful"
        );
    }
    public AuthResponse driverLogin(LoginRequest request) {

        Driver driver = driverRepository.findByPhoneNumber(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid driver credentials"));

        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new InvalidCredentialsException("Invalid driver credentials");
        }

        String token = jwtUtil.generateToken(
                driver.getPhoneNumber(),
                "DRIVER"
        );

        return new AuthResponse(
                token,
                "DRIVER",
                "Driver login successful"
        );
    }
}