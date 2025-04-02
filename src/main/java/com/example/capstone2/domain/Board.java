package com.example.capstone2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📌게시글 작성자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private String title;
    private String description;

    // 📌좋아요 개수
    private int likeCount;

    // 📌서비스 가격
    private int creditPrice;

    // 📌모집 완료 여부 (true = 모집 완료, false = 모집 중)
    private boolean isClosed;

    @Enumerated(EnumType.STRING)
    private Category category;

    // 📌게시글 작성 시간
    private LocalDateTime createdAt;

    // 📌게시글 수정 시간
    private LocalDateTime updatedAt;

    // 📌자동으로 생성 시간 저장
    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 📌게시글 수정 시 수정 시간 업데이트
    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
