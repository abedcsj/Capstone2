package com.example.capstone2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardParticipationDto {
    private Long id;
    private Long boardId;
    private Long userId;
    private boolean approved;
    private boolean serviceCompleted;
}
