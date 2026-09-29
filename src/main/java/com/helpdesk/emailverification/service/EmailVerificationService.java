package com.helpdesk.emailverification.service;

import com.helpdesk.emailverification.domain.EmailVerification;
import com.helpdesk.emailverification.mapper.EmailVerificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationMapper emailVerificationMapper;
    private final VerificationMailSender mailSender;

    @Value("${app.mail.verification-code-expire-minutes}")
    private int expireMinutes;

    /**
     * 6자리 인증번호를 생성해 DB에 저장하고, 해당 이메일로 발송을 요청한다.
     * 실제 메일 발송은 비동기(VerificationMailSender)로 넘어가므로, 이 메서드는
     * DB 저장이 끝나는 즉시 리턴한다.
     */
    @Transactional
    public void sendVerificationCode(String email) {
        String code = generateCode();

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(expireMinutes))
                .build();

        emailVerificationMapper.insertVerification(verification);

        mailSender.sendAsync(email, code);
    }

    /**
     * 사용자가 입력한 코드가 유효한지 확인하고, 맞으면 인증 완료 처리한다.
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        return emailVerificationMapper.findLatestValidCode(email, code)
                .map(v -> {
                    emailVerificationMapper.markVerified(v.getVerificationId());
                    return true;
                })
                .orElse(false);
    }

    /**
     * 회원가입 처리 직전, 이 이메일이 실제로 인증을 마쳤는지 최종 확인한다.
     */
    public boolean isEmailVerified(String email) {
        return emailVerificationMapper.countVerifiedEmail(email) > 0;
    }

    private String generateCode() {
        int number = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", number);
    }
}