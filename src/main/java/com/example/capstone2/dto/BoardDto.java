package com.example.capstone2.dto;

import com.example.capstone2.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
