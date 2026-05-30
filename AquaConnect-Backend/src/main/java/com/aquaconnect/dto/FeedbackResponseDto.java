package com.aquaconnect.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponseDto {

    private Long feedbackId;
    private String userName;
    private String branchName;
    private Long bookingId;
    private Integer rating;
    private String message;
    private LocalDateTime createdAt;
}