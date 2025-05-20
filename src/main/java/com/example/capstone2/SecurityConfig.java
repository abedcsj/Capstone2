package com.example.capstone2;

import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.security.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final ExternalProperties externalProperties;
    private final UserRepository userRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http,
            AuthenticationManager authenticationManager
    ) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager, objectMapper, authService, externalProperties
        );
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // ✅ 요거 추가!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // 인증 없이 접근 가능한 경로들
//                        .requestMatchers("/api/auth/**", "/api/token/**", "/api/users/register").permitAll()
//                        // 관리자 전용
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        // 그 외 모든 요청은 USER 또는 ADMIN 권한만 있으면 접근 가능
//                        .anyRequest().hasAnyRole("USER", "ADMIN")
//                ) // 프론트 구현시 에러나서 원래 코드 주석처리
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/",
                                        "/login",
                                        "/register",
                                        "/createboard",
                                        "/board/show/**",
                                        "/boardpage",
                                        "/board/modify/**",
                                        "/mypage",
                                        "/css/**",
                                        "/js/**",
                                        "/image/**",
                                        "/favicon.ico",
                                        "/api/users/register",
                                        "/board/create",
                                        "/api/auth/login").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                        //                        .requestMatchers("/", "/login", "/createboard", "api/users/register", "/register", "/mypage" , "/boardpage", "/api/auth/login", "/css/**", "/js/**").permitAll()

                )

                .addFilter(jwtAuthenticationFilter)
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, authService, externalProperties))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 모든 origin 허용, 필요시 도메인 명시
        config.addAllowedMethod("*"); // GET, POST, PUT 등 전부 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.setAllowCredentials(true); // 자격 정보 포함 허용
        config.addExposedHeader("Authorization"); // 🔥 여기!
        config.addExposedHeader("Authorization-refresh"); // 🔥 여기도!

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
