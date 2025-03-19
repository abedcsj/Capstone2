package com.example.capstone2.service;

import com.example.capstone2.domain.Credit;
import com.example.capstone2.domain.CreditStatus;
import com.example.capstone2.domain.CreditType;
import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.repository.CreditRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;
    private final UserRepository userRepository;

    public List<CreditDto> getCreditsByFromUserId(Long fromUserId) {
        return creditRepository.findByFromUserId(fromUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CreditDto> getCreditsByToUserId(Long toUserId) {
        return creditRepository.findByToUserId(toUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public CreditDto createCredit(CreditDto creditDTO) {
        Credit credit = new Credit();
        credit.setFromUser(userRepository.findById(creditDTO.getFromUserId())
                .orElseThrow(() -> new RuntimeException("보내는 사용자를 찾을 수 없음")));
        credit.setToUser(userRepository.findById(creditDTO.getToUserId())
                .orElseThrow(() -> new RuntimeException("받는 사용자를 찾을 수 없음")));
        credit.setAmount(creditDTO.getAmount());
        credit.setType(creditDTO.getType());
        credit.setStatus(CreditStatus.PENDING);
        credit.setTransactionTime(LocalDateTime.now()); // 거래 시간 자동 설정

        credit = creditRepository.save(credit);
        return convertToDto(credit);
    }

    @Transactional
    public CreditDto updateCreditStatus(Long creditId, CreditStatus status) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 크레딧이 존재하지 않음"));

        credit.setStatus(status);
        credit = creditRepository.save(credit);
        return convertToDto(credit);
    }


    // 📌 특정 사용자의 크레딧 내역 조회 (보낸 & 받은 내역 포함)
    public List<CreditDto> getUserCredits(Long userId) {
        List<Credit> sentCredits = creditRepository.findByFromUserId(userId);
        List<Credit> receivedCredits = creditRepository.findByToUserId(userId);

        return Stream.concat(sentCredits.stream(), receivedCredits.stream()) // ✅ 두 개 리스트 합치기
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private CreditDto convertToDto(Credit credit) {
        return new CreditDto(
                credit.getId(),
                credit.getFromUser().getId(),
                credit.getToUser().getId(),
                credit.getAmount(),
                credit.getType(),
                credit.getStatus(),
                credit.getTransactionTime()
        );
    }
}
