package com.example.capstone2.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 요청 (비즈니스 로직 예외)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage()); // 400
    }

    // 권한 문제 (SecurityException)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity(SecurityException e) {
        return ResponseEntity.status(403).body(e.getMessage()); // 403
    }

    // 잘못된 데이터 요청 (엔티티 없음 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage()); // 400
    }

    // 그 외 모든 에러 (예상 못한 서버 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        e.printStackTrace(); // 서버 콘솔에 로그 찍기
        return ResponseEntity.internalServerError().body("서버 내부 오류가 발생했습니다."); // 500
    }
}
