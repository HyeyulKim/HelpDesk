package com.helpdesk.emailverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {
    private Long verificationId;
    private String email;
    private String code;
    private boolean verified;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}