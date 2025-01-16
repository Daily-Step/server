package com.challenge.api.service.member;

import com.challenge.api.service.member.request.CheckNicknameServiceRequest;
import com.challenge.api.service.member.request.UpdateBirthServiceRequest;
import com.challenge.api.service.member.request.UpdateGenderServiceRequest;
import com.challenge.api.service.member.request.UpdateJobServiceRequest;
import com.challenge.api.service.member.request.UpdateJobYearServiceRequest;
import com.challenge.api.service.member.request.UpdateNicknameServiceRequest;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.api.service.member.response.MyPageResponse;
import com.challenge.api.service.s3.S3ClientService;
import com.challenge.domain.challenge.ChallengeQueryRepository;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChallengeQueryRepository challengeQueryRepository;
    private final JobRepository jobRepository;
    private final S3ClientService s3ClientService;

    /**
     * 회원 정보 조회 메소드
     *
     * @param member
     * @return
     */
    public MemberInfoResponse getMemberInfo(Member member) {
        return MemberInfoResponse.of(member);
    }

    /**
     * 회원 프로필 사진 조회 메소드
     *
     * @param member
     * @return
     */
    public String getMemberProfileImg(Member member) {
        return member.getProfileImg();
    }

    /**
     * 회원 마이페이지 조회 메소드
     *
     * @param member
     * @return
     */
    public MyPageResponse getMyPageInfo(Member member) {
        Long ongoing = challengeQueryRepository.countOngoingChallengesBy(member);
        Long succeed = challengeQueryRepository.countSucceedChallengesBy(member);
        Long total = challengeQueryRepository.countAllChallengesBy(member);
        return MyPageResponse.of(member, ongoing, succeed, total);
    }

    /**
     * 해당 닉네임이 사용 가능 여부를 조회하는 메소드
     *
     * @param request
     * @return
     */
    public String checkNicknameIsValid(CheckNicknameServiceRequest request) {
        String nickname = request.getNickname();

        // 다른 회원이 사용중인 닉네임인 경우
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            throw new GlobalException(ErrorCode.DUPLICATED_NICKNAME);
        }

        return "사용 가능한 닉네임입니다.";
    }

    /**
     * 닉네임 수정 메소드
     *
     * @param member
     * @param request
     * @return
     */
    @Transactional
    public String updateNickname(Member member, UpdateNicknameServiceRequest request) {
        String nickname = request.getNickname();

        // 다른 회원이 사용중인 닉네임인 경우
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            throw new GlobalException(ErrorCode.DUPLICATED_NICKNAME);
        }

        // 닉네임 수정
        member.updateNickname(nickname);

        return "닉네임 수정 성공";
    }

    /**
     * 생년월일 수정 메소드
     *
     * @param member
     * @param request
     * @return
     */
    @Transactional
    public String updateBirth(Member member, UpdateBirthServiceRequest request) {
        member.updateBirth(request.getBirth());

        return "생년월일 수정 성공";
    }

    /**
     * 성별 수정 메소드
     *
     * @param member
     * @param request
     * @return
     */
    @Transactional
    public String updateGender(Member member, UpdateGenderServiceRequest request) {
        member.updateGender(request.getGender());

        return "성별 수정 성공";
    }

    /**
     * 직업 수정 메소드
     *
     * @param member
     * @param request
     * @return
     */
    @Transactional
    public String updateJob(Member member, UpdateJobServiceRequest request) {
        if (request.getJobId() == 0) {
            member.updateJob(null);
        } else {
            // 직무 데이터 조회
            Job job = jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));

            member.updateJob(job);
        }

        return "직업 수정 성공";
    }

    /**
     * 연차 수정 메소드
     *
     * @param member
     * @param request
     * @return
     */
    @Transactional
    public String updateJobYear(Member member, UpdateJobYearServiceRequest request) {
        if (request.getYearId() == 0) {
            member.updateJobYear(null);
        } else {
            JobYear jobYear = JobYear.of(request.getYearId());
            member.updateJobYear(jobYear);
        }

        return "연차 수정 성공";
    }

    /**
     * 회원 프로필 사진 업로드 메소드
     *
     * @param member
     * @param image
     * @return
     */
    @Transactional
    public String uploadProfileImg(Member member, MultipartFile image) {
        // s3 이미지 업로드
        String imgUrl = s3ClientService.upload(image);

        // member 엔티티 업데이트
        member.updateProfileImg(imgUrl);

        return imgUrl;
    }

    /**
     * push 알림 받기 여부 수정 메소드
     *
     * @param member
     * @return
     */
    @Transactional
    public String updatePushReceive(Member member) {
        member.updateNotificationReceived();

        return "push 알림 여부 수정 성공";
    }

}
