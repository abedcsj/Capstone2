package com.example.capstone2.controller;

import com.example.capstone2.dto.UserDto;
import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.security.PrincipalDetails;
import com.example.capstone2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    //회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 username입니다.");
        }
        userService.register(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
    //사용자 정보 수정
    @PatchMapping
    public ResponseEntity<String> update(@AuthenticationPrincipal PrincipalDetails principal, @RequestBody UserDto userDto) {
        userService.updateUser(principal.getUser().getId(), userDto);
        return ResponseEntity.ok("사용자 정보가 수정되었습니다.");
    }
    //사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyInfo(@AuthenticationPrincipal PrincipalDetails principal) {
        return ResponseEntity.ok(userService.getUserById(principal.getUser().getId()));
    }
    //사용자 탈퇴
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal PrincipalDetails principal) {
        userService.deleteUser(principal.getUser().getId());
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
}
