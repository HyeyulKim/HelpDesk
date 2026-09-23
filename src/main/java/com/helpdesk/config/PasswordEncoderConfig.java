package com.helpdesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder를 SecurityConfig 밖으로 따로 뺀 이유:
 * SecurityConfig가 CustomOAuth2UserService(생성자 주입)를 필요로 하는데,
 * CustomOAuth2UserService는 PasswordEncoder를 필요로 하고,
 * PasswordEncoder가 SecurityConfig 안의 @Bean이면 SecurityConfig 자기 자신이
 * 완성되기 전에 자기 자신이 필요해지는 순환참조(BeanCurrentlyInCreationException)가 생긴다.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}