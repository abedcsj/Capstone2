package com.example.capstone2.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {
    // 요청용
    private Long id;              
    private Long reviewerId;
    private Long revieweeId;
    private String content;
    private int rating;

    // 응답용
    private LocalDateTime createdAt;
}
