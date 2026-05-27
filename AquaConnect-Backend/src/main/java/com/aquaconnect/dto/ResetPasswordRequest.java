package com.aquaconnect.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String role;

    private String identifier;

    private String lastFourDigits;

    private String newPassword;

    private String confirmPassword;
}