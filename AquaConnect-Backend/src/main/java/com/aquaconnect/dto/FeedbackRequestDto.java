package com.aquaconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequestDto {
    private Long bookingId;
    private Integer rating;
    private String message;
}