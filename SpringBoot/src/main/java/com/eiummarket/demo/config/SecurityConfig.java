package com.eiummarket.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS 허용(아래 corsConfigurationSource()와 연동)
            .cors(Customizer.withDefaults())

            // CSRF 비활성화 (API/토큰 기반 서비스에 권장)
            .csrf(csrf -> csrf.disable())

            // 세션 미사용
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 모든 요청 허용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/**",
                    "/h2-console/**",
                    "/error",
                    "/**" // 전체 허용
                ).permitAll()
                .anyRequest().permitAll()
            )

            // H2 콘솔 탑재 시 frame 옵션 해제
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            // 폼 로그인/HTTP Basic 비활성화 (완전 공개 API 형태)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }

    // 모든 오리진/헤더/메서드 허용 CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 전체 도메인 허용
        config.setAllowedOriginPatterns(List.of("*"));

        // 전체 메서드 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 전체 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        // 쿠키/자격증명은 비허용(필요 시 true로 변경)
        config.setAllowCredentials(false);

        // 캐시 시간(초) - 선택
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}