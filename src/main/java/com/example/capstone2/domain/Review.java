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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 📌리뷰 작성자 (서비스 이용자)
    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
    // 📌리뷰 대상 (서비스 제공자)
    @ManyToOne
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;
    // 📌리뷰 내용
    private String content;
    // 📌별점 (1~5)
    private int rating;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
