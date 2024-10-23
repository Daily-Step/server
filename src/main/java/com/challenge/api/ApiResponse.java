package com.challenge.api;

import com.challenge.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    // 실패 응답
    // {
    //   "status": 400,
    //   "message": "아이디는 필수입니다.",
    //   "code": "USER_4001",
    //   "url": "/api/v1/users/new",
    //   "data": null
    // }

    // 성공 응답
    // {
    //   "status": 200,
    //   "message": "OK",
    //   "code": null,
    //   "url": null,
    //   "data": {
    //      "id": "1",
    //   }
    // }

    private final int status;
    private final String message;
    private final String code;
    private final String url;
    private final T data;

    @Builder
    private ApiResponse(HttpStatus status, String message, String code, String url, T data) {
        this.status = status.value();
        this.message = message;
        this.code = code;
        this.url = url;
        this.data = data;
    }

    // 실패 응답
    public static <T> ApiResponse<T> of(ErrorCode errorCode, String url, T data) {
        return ApiResponse.<T>builder()
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .url(url)
                .data(data)
                .build();
    }

    // 성공응답
    public static <T> ApiResponse<T> of(HttpStatus status, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(status.name())
                .code(null)
                .url(null)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }

}

