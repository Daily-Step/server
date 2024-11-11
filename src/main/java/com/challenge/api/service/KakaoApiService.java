package com.challenge.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiService {

    @Value("${social.kakao.apikey}")
    private String kakaoApiKey;

    @Value("${social.kakao.redirect_uri}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate;

    private static final String ACCESS_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";

    public String getAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", kakaoApiKey);
        parameters.add("redirect_uri", kakaoRedirectUri);
        parameters.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    ACCESS_TOKEN_REQUEST_URL,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                JSONObject jsonResponse = new JSONObject(response);

                String accessToken = jsonResponse.getString("access_token");
                String refreshToken = jsonResponse.getString("refresh_token");

                log.info("response JSON: {}", responseBody);
                log.info("access token: {}", accessToken);
                log.info("refresh token: {}", refreshToken);

                return accessToken;
            } else {
                log.error("Failed to get access token. Status code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception occurred while getting access token: ", e);
        }

        return null;
    }

}
