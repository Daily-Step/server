package com.challenge.api.controller;

import com.challenge.api.ApiResponse;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public ApiResponse<String> test() {
        throw new GlobalException(ErrorCode.COMMON_NETWORK_ERROR);
    }

}
