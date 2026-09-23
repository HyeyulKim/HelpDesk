package com.helpdesk.emailverification.mapper;

import com.helpdesk.emailverification.domain.EmailVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface EmailVerificationMapper {

    void insertVerification(EmailVerification verification);

    /**
     * 해당 이메일로 가장 최근에 발급된, 아직 만료되지 않은 미인증 코드를 조회한다.
     */
    Optional<EmailVerification> findLatestValidCode(@Param("email") String email, @Param("code") String code);

    void markVerified(@Param("verificationId") Long verificationId);

    /**
     * 회원가입 직전, 해당 이메일이 실제로 인증 완료된 상태인지 최종 확인한다.
     */
    int countVerifiedEmail(@Param("email") String email);
}