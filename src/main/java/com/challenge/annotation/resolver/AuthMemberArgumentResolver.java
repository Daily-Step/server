package com.challenge.annotation.resolver;

import com.challenge.annotation.AuthMember;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @AuthMember 어노테이션이 붙어 있는지 여부
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(AuthMember.class);

        // 파라미터 타입이 Member 클래스인지 여부
        boolean hasMemberClass = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasParameterAnnotation && hasMemberClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Long memberId = (Long) request.getAttribute("memberId");
        if (memberId == null) {
            throw new GlobalException(ErrorCode.MEMBER_EXTRACTION_FAILED);
        }

        return memberRepository.findById(memberId).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }

}
