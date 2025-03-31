package com.example.capstone2;

import com.example.capstone2.domain.Role;
import com.example.capstone2.domain.User;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            String adminEmail = "admin@example.com";
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("admin1234")) // 원하는 비번
                        .role(Role.ADMIN)
                        .credit(100)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ 관리자 계정(admin) 생성 완료!");
            }
        };
    }
}
