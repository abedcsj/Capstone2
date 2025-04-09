package com.example.capstone2.repository;

import com.example.capstone2.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);
    List<Review> findByRevieweeId(Long revieweeId);
    // 평균 평점 계산 쿼리
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :revieweeId")
    Double findAverageRatingByRevieweeId(Long revieweeId);

    // (선택) 게시글 기준 평균 평점
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.board.id = :boardId")
    Double findAverageRatingByBoardId(Long boardId);
}