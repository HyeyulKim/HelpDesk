package com.helpdesk.emailverification.service;

import com.helpdesk.emailverification.domain.EmailVerification;
import com.helpdesk.emailverification.mapper.EmailVerificationMapper;
import com.helpdesk.emailverification.oauth.GmailOAuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationMapper emailVerificationMapper;
    private final GmailOAuthTokenProvider tokenProvider;

    @Value("${app.mail.verification-code-expire-minutes}")
    private int expireMinutes;

    @Value("${app.mail.sender-email}")
    private String senderEmail;

    /**
     * 6자리 인증번호를 생성해 DB에 저장하고, 해당 이메일로 발송한다.
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

        sendMailViaOAuth(email, code);
    }

    /**
     * Gmail OAuth2(XOAUTH2) 방식으로 SMTP 서버에 직접 연결해 메일을 발송한다.
     * (일반 비밀번호 인증이 아니라, Access Token을 비밀번호 자리에 넣어 인증)
     */
    private void sendMailViaOAuth(String toEmail, String code) {
        try {
            String accessToken = tokenProvider.getAccessToken();

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

            Session session = Session.getInstance(props);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[HelpDesk] 이메일 인증번호");
            message.setText(
                    "HelpDesk 회원가입을 위한 인증번호는 다음과 같습니다.\n\n"
                            + code + "\n\n"
                            + expireMinutes + "분 이내에 입력해주세요."
            );

            Transport transport = session.getTransport("smtp");
            // XOAUTH2에서는 두 번째 인자(password 자리)에 Access Token을 넣는다
            transport.connect("smtp.gmail.com", senderEmail, accessToken);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            throw new IllegalStateException("이메일 발송 중 오류가 발생했습니다.", e);
        }
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