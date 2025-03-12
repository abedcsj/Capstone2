package com.example.capstone2.controller;

import com.example.capstone2.domain.CreditStatus;
import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    // 📌 크레딧 생성 API
    @PostMapping
    public ResponseEntity<CreditDto> createCredit(@RequestBody CreditDto creditDTO) {
        return ResponseEntity.ok(creditService.createCredit(creditDTO));
    }

    // 📌 특정 사용자가 보낸 크레딧 목록 조회 API
    @GetMapping("/from/{userId}")
    public ResponseEntity<List<CreditDto>> getCreditsByFromUser(@PathVariable Long userId) {
        return ResponseEntity.ok(creditService.getCreditsByFromUserId(userId));
    }

    // 📌 특정 사용자가 받은 크레딧 목록 조회 API
    @GetMapping("/to/{userId}")
    public ResponseEntity<List<CreditDto>> getCreditsByToUser(@PathVariable Long userId) {
        return ResponseEntity.ok(creditService.getCreditsByToUserId(userId));
    }

    // 📌 크레딧 상태 변경 API (PENDING -> COMPLETED 등)
    @PutMapping("/{creditId}/status")
    public ResponseEntity<CreditDto> updateCreditStatus(@PathVariable Long creditId, @RequestParam CreditStatus status) {
        CreditDto updatedCredit = creditService.updateCreditStatus(creditId, status);
        if (updatedCredit != null) {
            return ResponseEntity.ok(updatedCredit);
        }
        return ResponseEntity.notFound().build();
    }
}
