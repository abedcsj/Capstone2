package com.example.capstone2.controller;

import com.example.capstone2.dto.ReviewDto;
import com.example.capstone2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
  
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto dto) {
        ReviewDto response = reviewService.createReview(dto);
        return ResponseEntity.ok(response);
    }

}
