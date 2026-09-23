package com.helpdesk.emailverification.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Refresh Token을 이용해 Gmail 발송용 Access Token을 발급/갱신한다.
 * Access Token은 보통 1시간 후 만료되므로, 만료 임박 시에만 새로 발급받아 캐싱한다.
 */
@Component
@RequiredArgsConstructor
public class GmailOAuthTokenProvider {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Value("${app.mail.oauth.client-id}")
    private String clientId;

    @Value("${app.mail.oauth.client-secret}")
    private String clientSecret;

    @Value("${app.mail.oauth.refresh-token}")
    private String refreshToken;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final AtomicReference<String> cachedAccessToken = new AtomicReference<>();
    private volatile Instant expiresAt = Instant.EPOCH;

    public synchronized String getAccessToken() {
        if (Instant.now().isBefore(expiresAt.minusSeconds(60))) {
            return cachedAccessToken.get();
        }
        return refreshAccessToken();
    }

    private String refreshAccessToken() {
        try {
            String form = "client_id=" + encode(clientId)
                    + "&client_secret=" + encode(clientSecret)
                    + "&refresh_token=" + encode(refreshToken)
                    + "&grant_type=refresh_token";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString(form))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Gmail OAuth 토큰 갱신 실패: " + response.body());
            }

            JsonNode json = objectMapper.readTree(response.body());
            String accessToken = json.get("access_token").asText();
            int expiresIn = json.get("expires_in").asInt();

            cachedAccessToken.set(accessToken);
            expiresAt = Instant.now().plusSeconds(expiresIn);

            return accessToken;
        } catch (Exception e) {
            throw new IllegalStateException("Gmail OAuth 토큰 갱신 중 오류가 발생했습니다.", e);
        }
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}