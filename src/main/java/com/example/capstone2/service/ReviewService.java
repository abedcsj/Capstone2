package com.example.capstone2.service;

import com.example.capstone2.domain.Review;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.ReviewDto;
import com.example.capstone2.repository.ReviewRepository;
import com.example.capstone2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto createReview(ReviewDto dto) {
        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByReviewerIdAndRevieweeId(dto.getReviewerId(), dto.getRevieweeId())) {
            throw new IllegalStateException("이미 리뷰를 작성하였습니다.");
        }

        // 사용자 조회
        User reviewer = userRepository.findById(dto.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 작성자 ID가 유효하지 않습니다!"));

        User reviewee = userRepository.findById(dto.getRevieweeId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 대상자 ID가 유효하지 않습니다!"));

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());

        Review savedReview = reviewRepository.save(review);

        // 응답 DTO
        return ReviewDto.builder()
                .id(savedReview.getId())
                .reviewerId(savedReview.getReviewer().getId())
                .revieweeId(savedReview.getReviewee().getId())
                .content(savedReview.getContent())
                .rating(savedReview.getRating())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }
}
