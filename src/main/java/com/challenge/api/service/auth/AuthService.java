package com.challenge.api.service.auth;

import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.api.service.auth.request.ReissueTokenServiceRequest;
import com.challenge.api.service.auth.response.LoginResponse;
import com.challenge.api.service.auth.response.ReissueTokenResponse;
import com.challenge.api.service.auth.response.SocialInfoResponse;
import com.challenge.api.validator.AuthValidator;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.JobYear;
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

    private final AuthValidator authValidator;
    private final KakaoApiService kakaoApiService;
    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    private final JwtUtil jwtUtil;

    /**
     * 카카오 로그인 메소드
     * kakao access token을 이용해 사용자 정보 조회 jwt access token 발급
     *
     * @param request
     * @return
     */
    public LoginResponse kakaoLogin(KakaoLoginServiceRequest request) {
        // 카카오 서버로부터 사용자 정보 가져오기
        SocialInfoResponse userInfo = kakaoApiService.getUserInfo(request.getAccessToken());

        // 회원 존재 여부 검증
        authValidator.validateMemberExistsBySocialInfo(userInfo);

        // socialId와 socialType에 해당하는 회원 조회
        Member member = memberRepository.findBySocialIdAndLoginType(userInfo.getSocialId(), LoginType.KAKAO);

        // 토큰 발급
        return generateLoginResponse(member.getId());
    }

    /**
     * 카카오 회원가입 메소드
     * kakao access token을 이용해 사용자 정보 조회 및 사용자 데이터 저장 후 jwt access token 발급
     *
     * @param request
     * @return
     */
    @Transactional
    public LoginResponse kakaoSignin(KakaoSigninServiceRequest request) {
        // 카카오 서버로부터 사용자 정보 가져오기
        SocialInfoResponse userInfo = kakaoApiService.getUserInfo(request.getAccessToken());

        // 이미 존재하는 회원이 아닌지 검증
        authValidator.validateMemberNotExistsBySocialInfo(userInfo);

        // 닉네임 중복 여부 검증
        authValidator.validateUniqueNickname(request.getNickname());

        // 직무 데이터 설정
        Job job;
        if (request.getJobId() == 0) {
            job = null;
        } else {
            job = jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
        }

        // 연차 enum 데이터 설정
        JobYear jobYear;
        if (request.getYearId() == 0) {
            jobYear = null;
        } else {
            jobYear = JobYear.of(request.getYearId());
        }

        // member 엔티티 생성 및 저장
        Member member = Member.create(userInfo.getSocialId(), userInfo.getEmail(), request.getNickname(),
                request.getBirth(), request.getGender(), jobYear, LoginType.KAKAO, job);
        Member savedMember = memberRepository.save(member);

        // 토큰 발급
        return generateLoginResponse(savedMember.getId());
    }

    /**
     * access token 및 refresh token 재발급 메소드
     *
     * @param request
     * @return
     */
    public ReissueTokenResponse reissueToken(ReissueTokenServiceRequest request) {
        // 요청한 refresh token 검증
        try {
            jwtUtil.validateToken(request.getRefreshToken());
        } catch (GlobalException e) {
            // refresh 토큰이 만료된 경우
            if (ErrorCode.EXPIRED_JWT_EXCEPTION.getCode().equals(e.getCode())) {
                // refresh token 만료 에러 발생
                throw new GlobalException(ErrorCode.EXPIRED_REFRESH_TOKEN);
            }
            throw e;
        }

        // refresh token에서 memberId 추출
        Long memberId = jwtUtil.getMemberId(request.getRefreshToken());

        // 회원 존재 여부 검증
        authValidator.validateMemberExistsById(memberId);

        // 토큰 발급
        String accessToken = jwtUtil.createAccessToken(memberId);
        String refreshToken = jwtUtil.createRefreshToken(memberId);
        Long tokenExpirationTime = jwtUtil.getTokenExpirationTime(accessToken);

        return ReissueTokenResponse.of(accessToken, refreshToken, tokenExpirationTime);
    }


    private LoginResponse generateLoginResponse(Long memberId) {
        String accessToken = jwtUtil.createAccessToken(memberId);
        String refreshToken = jwtUtil.createRefreshToken(memberId);
        Long tokenExpirationTime = jwtUtil.getTokenExpirationTime(accessToken);

        return LoginResponse.of(memberId, accessToken, refreshToken, tokenExpirationTime);
    }

}
