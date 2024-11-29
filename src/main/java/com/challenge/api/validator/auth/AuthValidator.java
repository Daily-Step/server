package com.challenge.api.validator.auth;

import com.challenge.api.service.auth.response.SocialInfoResponse;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthValidator {

    private final MemberRepository memberRepository;

    /**
     * 소셜 회원 정보에 해당하는 사용자가 존재하는지 검증하는 메소드
     *
     * @param userInfo
     */
    public void validateMemberExists(SocialInfoResponse userInfo) {
        boolean exists = memberRepository.existsBySocialIdAndLoginType(userInfo.getSocialId(), userInfo.getLoginType());
        if (!exists) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    /**
     * 소셜 회원 정보에 해당하는 사용자가 존재하지 않는지 검증하는 메소드
     *
     * @param userInfo
     */
    public void validateMemberNotExists(SocialInfoResponse userInfo) {
        boolean exists = memberRepository.existsBySocialIdAndLoginType(userInfo.getSocialId(), userInfo.getLoginType());
        if (exists) {
            throw new GlobalException(ErrorCode.MEMBER_EXISTS);
        }
    }

    public void validateUniqueNickname(String nickname) {
        boolean existsByNickname = memberRepository.existsByNickname(nickname);
        if (existsByNickname) {
            throw new GlobalException(ErrorCode.DUPLICATED_NICKNAME);
        }
    }

}
