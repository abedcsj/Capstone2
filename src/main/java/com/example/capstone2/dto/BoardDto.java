package com.example.capstone2.dto;

import com.example.capstone2.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private boolean isRequest;
    private int likeCount;
    private Category category;
    private int creditPrice;
    private boolean isClosed; // 모집 완료 여부
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
}
