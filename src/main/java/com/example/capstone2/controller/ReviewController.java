package com.example.capstone2.controller;

import com.example.capstone2.dto.ReviewDto;
import com.example.capstone2.security.PrincipalDetails;
import com.example.capstone2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto dto,
                                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        ReviewDto created = reviewService.createReview(dto, userId);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id,
                                                  @RequestBody ReviewDto dto,
                                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        ReviewDto updated = reviewService.updateReview(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/reviewee/{revieweeId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByReviewee(@PathVariable Long revieweeId) {
        return ResponseEntity.ok(reviewService.getReviewsByRevieweeId(revieweeId));
    }

    @GetMapping("/reviewee/{revieweeId}/average")
    public ResponseEntity<Double> getAverageByReviewee(@PathVariable Long revieweeId) {
        return ResponseEntity.ok(reviewService.getAverageRatingByRevieweeId(revieweeId));
    }

    @GetMapping("/board/{boardId}/average")
    public ResponseEntity<Double> getAverageByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(reviewService.getAverageRatingByBoardId(boardId));
    }
}
