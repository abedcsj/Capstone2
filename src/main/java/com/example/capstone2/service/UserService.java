package com.example.capstone2.service;

import com.example.capstone2.domain.Role;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.UserDto;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.USER) // 회원가입 시 권한은 항상 USER
                .credit(10)
                .build();

        userRepository.save(user);
    }


    public void updateUser(Long targetUserId, UserDto userDto) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 본인인지 확인
        validateIsSelf(targetUserId);

        if (!user.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        userRepository.save(user);
    }

    public void deleteUser(Long targetUserId) {
        validateIsSelf(targetUserId);

        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
        userRepository.deleteById(targetUserId);
    }

    public void deleteUserByAdmin(Long targetUserId) {
        validateIsAdmin();

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    public UserDto getUserById(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 본인이거나 관리자만 허용
        if (!isSelf(user.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public List<UserDto> getAllUsers() {
        validateIsAdmin();

        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }

    // ============================ 인증 & 권한 체크 헬퍼 ============================
    private void validateIsAdmin() {
        if (!hasRole("ROLE_ADMIN")) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }
    }

    private void validateIsSelf(Long userId) {
        if (!isSelf(userId)) {
            throw new AccessDeniedException("본인만 접근할 수 있습니다.");
        }
    }

    private boolean isSelf(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        Object principal = auth.getPrincipal();
        if (principal instanceof com.example.capstone2.security.PrincipalDetails details) {
            return Objects.equals(details.getUser().getId(), userId);
        }
        return false;
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null &&
                auth.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }
}
