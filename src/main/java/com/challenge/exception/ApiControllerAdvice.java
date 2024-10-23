package com.challenge.exception;

import com.challenge.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ApiControllerAdvice {

    // 400(Bad Request)
    //  - 요청이 구문적으로 잘못되었거나, 요청에 포함된 데이터가 서버의 요구 사항에 맞지 않는 경우입니다.
    //  - 입력 데이터 유효성 검사 실패는 일반적으로 이 상태 코드로 응답합니다.
    // 422(Unprocessable Entity)
    //  - 입력 데이터는 올바른 형식을 가지고 있지만, 비즈니스 로직에 의해 거부된 경우(예: 중복된 데이터, 유효하지 않은 데이터 상태).

    /**
     * GlobalException 예외 처리 ( ErrorCode 를 사용하여 예외 처리 )
     */
    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> globalException(GlobalException ex, HttpServletRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .status(ex.getStatus())
                .code(ex.getCode())
                .message(ex.getMessage())
                .url(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus().value()));
    }

    /**
     * @Valid 입력 데이터의 유효성 검사 실패 시 발생하는 예외 처리 ( 컨트롤러 DTO )
     */
    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> methodArgumentNotValidException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse
                .builder()
                .status(BAD_REQUEST)
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .code("VALID_ERROR")
                .url(request.getRequestURI())
                .data(null)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

}

