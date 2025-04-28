package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.ReviewDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
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
    private final BoardRepository boardRepository;
    private final BoardParticipationRepository boardParticipationRepository;

    @Transactional
    public ReviewDto createReview(ReviewDto dto, Long reviewerId) {
        if (reviewRepository.existsByReviewerIdAndRevieweeId(reviewerId, dto.getRevieweeId())) {
            throw new IllegalStateException("이미 리뷰를 작성하였습니다.");
        }

        if (!boardParticipationRepository.existsByBoardIdAndUserIdAndStatus(dto.getBoardId(), reviewerId, ParticipationStatus.APPROVED)) {
            throw new IllegalStateException("해당 게시판에 참여한 사용자만 리뷰를 작성할 수 있습니다.");
        }

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 작성자 정보 없음"));

        User reviewee = userRepository.findById(dto.getRevieweeId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 대상자 정보 없음"));

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보 없음"));

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setBoard(board);
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());

        Review savedReview = reviewRepository.save(review);
        return toDto(savedReview);
    }
    @Transactional
    public ReviewDto updateReview(Long id, ReviewDto dto, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if (!review.getReviewer().getId().equals(userId)) {
            throw new SecurityException("본인만 수정할 수 있습니다.");
        }

        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        return toDto(review);
    }
    @Transactional
    public void deleteReview(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if (!review.getReviewer().getId().equals(userId)) {
            throw new SecurityException("본인만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    // 리뷰 단건 조회
    @Transactional
    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));
        return toDto(review);
    }

    // 특정 사용자(서비스 제공자)에 대한 리뷰 목록 조회
    @Transactional
    public List<ReviewDto> getReviewsByRevieweeId(Long revieweeId) {
        List<Review> reviews = reviewRepository.findByRevieweeId(revieweeId);
        return reviews.stream().map(this::toDto).toList();
    }

    @Transactional
    public double getAverageRatingByRevieweeId(Long revieweeId) {
        Double average = reviewRepository.findAverageRatingByRevieweeId(revieweeId);
        return average != null ? average : 0.0;
    }

    // (선택) 게시글 기준
    @Transactional
    public double getAverageRatingByBoardId(Long boardId) {
        Double average = reviewRepository.findAverageRatingByBoardId(boardId);
        return average != null ? average : 0.0;
    }


    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .revieweeId(review.getReviewee().getId())
                .boardId(review.getBoard().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
