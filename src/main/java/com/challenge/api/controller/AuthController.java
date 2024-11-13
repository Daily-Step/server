package com.challenge.api.controller;

import com.challenge.api.ApiResponse;
import com.challenge.api.dto.AuthRequest;
import com.challenge.api.dto.AuthResponse;
import com.challenge.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ApiResponse<AuthResponse.loginDto> kakaoLogin(@RequestBody AuthRequest.kakaoLoginRequest request) {
        return ApiResponse.of(HttpStatus.OK, authService.kakaoLogin(request.getAccessToken()));
    }

}
