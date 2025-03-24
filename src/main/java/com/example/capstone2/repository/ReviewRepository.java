package com.example.capstone2.repository;

import com.example.capstone2.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 사용자가 봉사자에게 이미 리뷰를 남겼는지 확인
    boolean existsByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);
}
