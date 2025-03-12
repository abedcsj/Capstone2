package com.example.capstone2.dto;

import com.example.capstone2.domain.CreditStatus;
import com.example.capstone2.domain.CreditType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDto {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private int amount;
    private CreditType type;
    private CreditStatus status;
    private LocalDateTime transactionTime;
}
