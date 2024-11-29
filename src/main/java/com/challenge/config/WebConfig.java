package com.challenge.config;

import com.challenge.api.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final List<String> excludeEndpoints = Arrays.asList("/api/v1/auth/**");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // 인터셉터 적용할 endpoint
                .excludePathPatterns(excludeEndpoints); // 인터셉터 적용하지 않을 endpoint
    }

}
