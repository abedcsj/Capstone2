package com.example.capstone2.dto;

import com.example.capstone2.domain.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardParticipationDto {
    private Long id;
    private Long boardId;
    private Long userId;
    private ParticipationStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private int creditAmount;
}
