package com.challenge.api.service.auth;

import com.challenge.api.controller.auth.request.KakaoSigninRequest;
import com.challenge.api.service.auth.response.KakaoUserResponse;
import com.challenge.api.service.auth.response.LoginResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.domain.member.Year;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
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
    private final JobRepository jobRepository;
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
        Long memberId = member.getId();
        String accessToken = jwtUtil.createAccessToken(memberId);
        String refreshToken = jwtUtil.createRefreshToken(memberId);

        Long tokenExpirationTime = jwtUtil.getTokenExpirationTime(accessToken);

        return LoginResponse.of(memberId, accessToken, refreshToken, tokenExpirationTime);
    }

    @Transactional
    public LoginResponse kakaoSignin(KakaoSigninRequest request) {
        // 카카오 서버로부터 사용자 정보 가져오기
        KakaoUserResponse userInfo = kakaoApiService.getUserInfo(request.getAccessToken());

        // 이미 존재하는 회원이 아닌지 검증
        Optional<Member> existingMember = memberRepository.findBySocialIdAndLoginType(userInfo.getSocialId(),
                LoginType.KAKAO);

        if (existingMember.isPresent()) {
            throw new GlobalException(ErrorCode.MEMBER_EXISTS);
        }

        // 닉네임 중복 여부 검증
        boolean existsByNickname = memberRepository.existsByNickname(request.getNickname());
        if (existsByNickname) {
            throw new GlobalException(ErrorCode.DUPLICATED_NICKNAME);
        }

        // 직무 데이터 설정
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));

        // 연차 enum 데이터 설정
        Year year = Year.of(request.getYearId());

        // member 엔티티 생성 및 저장
        Member member = Member.create(userInfo.getSocialId(), userInfo.getEmail(), request.getNickname(),
                request.getBirth(), request.getGender(), year, LoginType.KAKAO, job);
        Member savedMember = memberRepository.save(member);

        // 토큰 발급
        Long memberId = savedMember.getId();
        String accessToken = jwtUtil.createAccessToken(memberId);
        String refreshToken = jwtUtil.createRefreshToken(memberId);

        Long tokenExpirationTime = jwtUtil.getTokenExpirationTime(accessToken);

        return LoginResponse.of(memberId, accessToken, refreshToken, tokenExpirationTime);
    }

}
