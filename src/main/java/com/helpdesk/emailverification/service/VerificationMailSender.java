package com.helpdesk.emailverification.service;

import com.helpdesk.emailverification.oauth.GmailOAuthTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationMailSender {

    private final GmailOAuthTokenProvider tokenProvider;

    @Value("${app.mail.sender-email}")
    private String senderEmail;

    @Value("${app.mail.verification-code-expire-minutes}")
    private int expireMinutes;

    /**
     * 인증번호 메일을 비동기로 발송한다. 호출한 쪽(컨트롤러)은 이 메서드가 끝나기를
     * 기다리지 않고 바로 응답을 돌려준다. 발송이 실패해도 화면에는 표시되지 않으므로
     * 로그로만 남긴다 (아래 "단점" 참고).
     */
    @Async("mailExecutor")
    public void sendAsync(String toEmail, String code) {
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
            transport.connect("smtp.gmail.com", senderEmail, accessToken);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            log.error("인증 메일 발송 실패: {}", toEmail, e);
        }
    }
}