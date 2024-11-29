package com.challenge.api.controller.auth;

import com.challenge.api.ApiResponse;
import com.challenge.api.controller.auth.request.KakaoLoginRequest;
import com.challenge.api.controller.auth.request.KakaoSigninRequest;
import com.challenge.api.service.auth.AuthService;
import com.challenge.api.service.auth.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return ApiResponse.ok(authService.kakaoLogin(request.toServiceRequest()));
    }

    @PostMapping("/signin/kakao")
    public ApiResponse<LoginResponse> kakaoSignin(@RequestBody @Valid KakaoSigninRequest request) {
        return ApiResponse.ok(authService.kakaoSignin(request.toServiceRequest()));
    }

}
