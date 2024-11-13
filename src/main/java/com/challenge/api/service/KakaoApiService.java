package com.challenge.api.service;

import com.challenge.api.dto.AuthResponse;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private static final String USER_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    /**
     * 인증 code를 가지고 카카오 API 서버로부터 access token을 받아오는 메소드
     *
     * @param code
     * @return
     */
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
            ResponseEntity<String> response = restTemplate.postForEntity(ACCESS_TOKEN_REQUEST_URL, entity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                JSONObject jsonResponse = new JSONObject(responseBody);

                String accessToken = jsonResponse.getString("access_token");
                String refreshToken = jsonResponse.getString("refresh_token");

//                log.info("response JSON: {}", responseBody);
                log.info("access token: {}", accessToken);
                log.info("refresh token: {}", refreshToken);

                return accessToken;
            }

            log.error("Failed to get access token. Status code: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Exception occurred while getting access token: ", e);
        }

        return null;
    }

    /**
     * access token을 가지고 카카오 사용자 정보를 불러오는 메소드
     *
     * @param accessToken
     * @return
     */
    public AuthResponse.kakaoResultDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(USER_INFO_REQUEST_URL, HttpMethod.GET, entity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                JSONObject jsonResponse = new JSONObject(responseBody);

                long id = jsonResponse.getLong("id");
                String email = jsonResponse.getJSONObject("kakao_account").getString("email");

                log.info("kakao user id: {}", id);
                log.info("kakao user email: {}", email);

                return AuthResponse.kakaoResultDto.builder().socialId(id).email(email).build();
            }
            
            log.error("Failed to get user info. Status code: {}", response.getStatusCode());
            throw new GlobalException(ErrorCode.KAKAO_REQ_FAILED);
        } catch (Exception e) {
            log.error("Exception occurred while getting user info: ", e);
            throw new GlobalException(ErrorCode.KAKAO_REQ_FAILED);
        }
    }

}
