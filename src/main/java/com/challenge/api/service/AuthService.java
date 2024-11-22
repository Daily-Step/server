package com.challenge.api.service;

import com.challenge.api.dto.AuthResponse;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoApiService kakaoApiService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    /**
     * 카카오 소셜 회원가입 및 로그인 메소드
     * kakao access token을 이용해 사용자 정보 조회 및 등록 후 jwt access token 발급
     *
     * @param kakaoAccessToken
     * @return
     */
    @Transactional
    public AuthResponse.authDto kakaoAuth(String kakaoAccessToken) {
        // 카카오 서버로부터 사용자 정보 가져오기
        AuthResponse.kakaoResultDto userInfo = kakaoApiService.getUserInfo(kakaoAccessToken);

        // socialId와 socialType에 해당하는 회원 존재 여부 확인
        Optional<Member> memberOptional = memberRepository.findBySocialIdAndLoginType(userInfo.getSocialId(),
                LoginType.KAKAO);

        if (memberOptional.isPresent()) { // 회원 존재하는 경우 로그인 처리
            return login(memberOptional.get());
        } else { // 회원 존재하지 않는 경우 회원가입 처리
            return kakaoRegister(userInfo);
        }
    }

    /**
     * 회원 로그인 메소드
     *
     * @param member
     * @return
     */
    private AuthResponse.authDto login(Member member) {
        String accessToken = jwtUtil.createAccessToken(member.getId());

        return AuthResponse.authDto.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtUtil.getTokenExpirationTime(accessToken))
                .build();
    }

    /**
     * 카카오 회원 등록 메소드
     *
     * @param userInfo
     * @return
     */
    private AuthResponse.authDto kakaoRegister(AuthResponse.kakaoResultDto userInfo) {
        // 회원 엔티티 생성 및 저장
        Member member = Member.builder()
                .socialId(userInfo.getSocialId())
                .email(userInfo.getEmail())
                .loginType(LoginType.KAKAO)
                .build();

        memberRepository.save(member);

        // 토큰 발급
        String accessToken = jwtUtil.createAccessToken(member.getId());

        return AuthResponse.authDto.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtUtil.getTokenExpirationTime(accessToken))
                .build();
    }

}
