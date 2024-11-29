package com.challenge.config;

import com.challenge.annotation.resolver.AuthMemberArgumentResolver;
import com.challenge.api.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthMemberArgumentResolver authMemberArgumentResolver;
    private final AuthInterceptor authInterceptor;
    private final List<String> excludeEndpoints = Arrays.asList("/api/v1/auth/**");

    // 인터셉터 설정
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // 인터셉터 적용할 endpoint
                .excludePathPatterns(excludeEndpoints); // 인터셉터 적용하지 않을 endpoint
    }

    // authMember 어노테이션 resolver 설정
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authMemberArgumentResolver);
    }

}
