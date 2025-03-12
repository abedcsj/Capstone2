package com.example.capstone2.service;

import com.example.capstone2.domain.Credit;
import com.example.capstone2.domain.CreditStatus;
import com.example.capstone2.domain.CreditType;
import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;

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
        credit.setAmount(creditDTO.getAmount());
        credit.setType(creditDTO.getType());
        credit.setStatus(CreditStatus.PENDING);
        credit = creditRepository.save(credit);
        return convertToDto(credit);
    }

    @Transactional
    public CreditDto updateCreditStatus(Long creditId, CreditStatus status) {
        Optional<Credit> creditOptional = creditRepository.findById(creditId);
        if (creditOptional.isPresent()) {
            Credit credit = creditOptional.get();
            credit.setStatus(status);
            credit = creditRepository.save(credit);
            return convertToDto(credit);
        }
        return null;
    }

    // 📌 특정 사용자의 크레딧 내역 조회 (보낸 & 받은 내역 포함)
    public List<CreditDto> getUserCredits(Long userId) {
        List<Credit> sentCredits = creditRepository.findByFromUserId(userId);
        List<Credit> receivedCredits = creditRepository.findByToUserId(userId);

        return sentCredits.stream()
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
