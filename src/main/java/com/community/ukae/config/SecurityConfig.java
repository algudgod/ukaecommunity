package com.community.ukae.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/kakao/**").authenticated() // 경로 인증 필요
                        .anyRequest().permitAll() // 그 외 모든 요청 허용
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 설정
                        .defaultSuccessUrl("/kakao/addUser", false) // 인증 성공 후 리다이렉트 경로
                        .failureUrl("/error") // 인증 실패 시 이동 경로
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}