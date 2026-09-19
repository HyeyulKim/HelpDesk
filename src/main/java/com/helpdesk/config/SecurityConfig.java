package com.helpdesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //UserService에서 @RequiredArgsConstructor로 PasswordEncoder passwordEncoder를 주입받아 passwordEncoder.encode(rawPassword)로 바로 쓸 수 있음
    //Spring Security 내부에서 이 빈을 자동으로 찾아서, 입력한 비밀번호를 암호화한 뒤 DB에 저장된 암호화 값과 비교
    @Bean
    public PasswordEncoder passwordEncoder() {//비밀번호를 암호화(해싱)하는 도구를 스프링 컨테이너에 등록
        return new BCryptPasswordEncoder();//같은 비밀번호를 넣어도 매번 다른 암호화 결과가 나오는 솔트(salt) 내장 해시 알고리즘
    }

    //이 메서드가 반환하는 SecurityFilterChain이 실제로 모든 요청을 가로채는 필터 목록
    //HttpSecurity: HTTP 요청에 대한 보안 규칙을 설정하는 빌더 객체. 스프링이 자동으로 주입
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(auth -> auth
                .antMatchers("/", "/user/signup", "/user/login", "/css/**", "/js/**").permitAll()//로그인 안 해도 접근 가능
                .antMatchers("/statistics/**").hasRole("AGENT")//담당자만 통계 대시보드 열람 가능
                .anyRequest().authenticated()//위에 나열되지 않은 나머지 모든 요청은 로그인 필수
            )
            .formLogin(form -> form
                .loginPage("/user/login")//로그인 안 한 사용자가 보호된 페이지에 접근하면 이 URL로 리다이렉트
                .loginProcessingUrl("/user/login")//로그인 폼이 제출(POST)되는 URL
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/user/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            );

        return http.build();
    }
}
