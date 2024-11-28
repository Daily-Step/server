package com.challenge.api.service.auth;

import com.challenge.api.controller.auth.response.KakaoUserResponse;
import com.challenge.api.controller.auth.response.LoginResponse;
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
     * 카카오 로그인 메소드
     * kakao access token을 이용해 사용자 정보 조회 jwt access token 발급
     *
     * @param kakaoAccessToken
     * @return
     */
    @Transactional
    public LoginResponse kakaoLogin(String kakaoAccessToken) {
        // 카카오 서버로부터 사용자 정보 가져오기
        KakaoUserResponse userInfo = kakaoApiService.getUserInfo(kakaoAccessToken);

        // socialId와 socialType에 해당하는 회원 조회
        Member member = memberRepository.findBySocialIdAndLoginType(userInfo.getSocialId(), LoginType.KAKAO)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        // 토큰 발급
        String accessToken = jwtUtil.createAccessToken(member.getId());
        String refreshToken = jwtUtil.createRefreshToken(member.getId());

        Long memberId = member.getId();
        Long tokenExpirationTime = jwtUtil.getTokenExpirationTime(accessToken);

        return LoginResponse.of(memberId, accessToken, refreshToken, tokenExpirationTime);
    }

}
