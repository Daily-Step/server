package com.challenge;

import com.challenge.annotation.resolver.AuthMemberArgumentResolver;
import com.challenge.api.controller.category.CategoryController;
import com.challenge.api.interceptor.AuthInterceptor;
import com.challenge.api.service.category.CategoryService;
import com.challenge.config.WebConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(value = {CategoryController.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {WebConfig.class, AuthMemberArgumentResolver.class, AuthInterceptor.class}
        ))
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CategoryService categoryService;

}
