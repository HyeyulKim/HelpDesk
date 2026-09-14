package com.helpdesk.user.service;

import com.helpdesk.user.domain.Role;
import com.helpdesk.user.domain.User;
import com.helpdesk.user.dto.SignupRequestDto;
import com.helpdesk.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequestDto dto) {
        if (userMapper.countByUsername(dto.getUsername()) > 0) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        if (userMapper.countByEmail(dto.getEmail()) > 0) {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");
        }

        // ADMIN은 회원가입 화면에서 선택 불가 (서버단 방어)
        Role role = (dto.getRole() == Role.AGENT) ? Role.AGENT : Role.USER;

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .role(role)
                .build();

        userMapper.insertUser(user);
    }
}
