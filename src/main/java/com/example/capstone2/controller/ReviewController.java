package com.example.capstone2.controller;

import com.example.capstone2.dto.ReviewDto;
import com.example.capstone2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @RequestBody ReviewDto dto) {
        return ResponseEntity.ok(reviewService.updateReview(id, dto));
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    // 특정 사용자(서비스 제공자)에 대한 리뷰 목록 조회
    @GetMapping("/user/{revieweeId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByReviewee(@PathVariable Long revieweeId) {
        List<ReviewDto> reviews = reviewService.getReviewsByRevieweeId(revieweeId);
        return ResponseEntity.ok(reviews);
    }
}