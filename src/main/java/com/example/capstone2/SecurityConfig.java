package com.example.capstone2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화 (테스트용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/board.html", "/credits.html", "/admin.html").permitAll() // 특정 HTML 접근 허용
                        .requestMatchers("/api/**").permitAll() // API 접근 허용
                        .requestMatchers("/**").permitAll() // 🔥 모든 정적 파일 접근 허용 (추가된 부분)
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .formLogin(form -> form.disable()) // 기본 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP 기본 인증 비활성화
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
