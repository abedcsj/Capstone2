package com.example.capstone2.service;

import com.example.capstone2.domain.Review;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.ReviewDto;
import com.example.capstone2.repository.ReviewRepository;
import com.example.capstone2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // 리뷰 생성
    @Transactional
    public ReviewDto createReview(ReviewDto dto) {
        if (reviewRepository.existsByReviewerIdAndRevieweeId(dto.getReviewerId(), dto.getRevieweeId())) {
            throw new IllegalStateException("이미 리뷰를 작성하였습니다.");
        }

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
        return toDto(savedReview);
    }

    // 리뷰 수정
    @Transactional
    public ReviewDto updateReview(Long id, ReviewDto dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        return toDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다.");
        }
        reviewRepository.deleteById(id);
    }

    // 리뷰 단건 조회
    @Transactional
    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));
        return toDto(review);
    }

    // 특정 사용자의 리뷰 목록 조회
    @Transactional
    public List<ReviewDto> getReviewsByRevieweeId(Long revieweeId) {
        List<Review> reviews = reviewRepository.findByRevieweeId(revieweeId);
        return reviews.stream().map(this::toDto).toList();
    }

    // Entity -> DTO
    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .revieweeId(review.getReviewee().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}