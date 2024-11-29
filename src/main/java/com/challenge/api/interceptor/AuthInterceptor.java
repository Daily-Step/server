package com.challenge.api.interceptor;

import com.challenge.api.ApiResponse;
import com.challenge.exception.GlobalException;
import com.challenge.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        try {
            // request에서 access token 추출
            String accessToken = jwtUtil.resolveToken(request);

            // access token 값 검증
            jwtUtil.validateToken(accessToken);

            // access token에서 memberId 추출
            Long memberId = jwtUtil.getMemberId(accessToken);

            // request 객체에 값 저장
            request.setAttribute("memberId", memberId);
        } catch (GlobalException exception) {
            sendErrorResponse(response, exception, request.getRequestURI());
            return false;
        }

        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, GlobalException exception,
                                   String requestUrl) throws IOException {
        // 응답 Content-Type 설정
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(exception.getStatus().value());

        // 에러 응답 생성
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .url(requestUrl)
                .build();

        // JSON 직렬화 및 응답 작성
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
