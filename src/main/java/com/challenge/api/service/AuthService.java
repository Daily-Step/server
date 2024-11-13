package com.challenge.api.service;

import com.challenge.api.dto.AuthResponse;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import com.challenge.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoApiService kakaoApiService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    /**
     * kakao access token을 이용해 사용자 정보 조회 및 jwt access token 발급
     *
     * @param kakaoAccessToken
     * @return
     */
    @Transactional
    public AuthResponse.loginDto kakaoLogin(String kakaoAccessToken) {
        // 카카오 서버로부터 사용자 정보 가져오기
        AuthResponse.kakaoResultDto userInfo = kakaoApiService.getUserInfo(kakaoAccessToken);

        // socialId와 socialType에 해당하는 회원 존재하는지 검증
        Member member = memberRepository.findBySocialIdAndLoginType(userInfo.getSocialId(), LoginType.KAKAO)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(member.getId());

        return AuthResponse.loginDto.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtUtil.getTokenExpirationTime(accessToken))
                .build();
    }

}
