package com.example.capstone2.repository;

import com.example.capstone2.domain.Credit;
import com.example.capstone2.domain.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByFromUserId(Long fromUserId);
    List<Credit> findByToUserId(Long toUserId);
    Optional<Credit> findByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, CreditStatus status);
}

