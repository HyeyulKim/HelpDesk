package com.helpdesk.security;

import com.helpdesk.user.domain.Role;
import com.helpdesk.user.domain.User;
import com.helpdesk.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * "Google로 로그인" 클릭 시 실제로 호출되는 서비스.
 * 구글에서 받은 이메일로 기존 회원인지 확인하고, 없으면 새로 가입시킨다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 구글에 실제로 사용자 정보를 요청

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userMapper.findByEmail(email);
        if (user == null) {
            user = registerNewGoogleUser(email, name);
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        userDetails.setAttributes(oAuth2User.getAttributes());
        return userDetails;
    }

    private User registerNewGoogleUser(String email, String name) {
        User newUser = User.builder()
                .username(email) // 구글 가입자는 이메일을 아이디로 사용 (항상 고유함)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 실제로 쓸 일 없는 비밀번호(로그인은 항상 구글로만)
                .name(name)
                .email(email)
                .role(Role.USER)
                .build();

        userMapper.insertUser(newUser);
        return newUser;
    }
}