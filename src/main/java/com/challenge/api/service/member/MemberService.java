package com.challenge.api.service.member;

import com.challenge.api.service.member.request.CheckNicknameServiceRequest;
import com.challenge.api.service.member.request.UpdateBirthServiceRequest;
import com.challenge.api.service.member.request.UpdateGenderServiceRequest;
import com.challenge.api.service.member.request.UpdateJobServiceRequest;
import com.challenge.api.service.member.request.UpdateNicknameServiceRequest;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;

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
    public String updateJob(Member member, UpdateJobServiceRequest request) {
        // 직무 데이터 조회
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));

        member.updateJob(job);

        return "직업 수정 성공";
    }

    /**
     * 회원 정보 조회 메소드
     *
     * @param member
     * @return
     */
    public MemberInfoResponse getMemberInfo(Member member) {
        return MemberInfoResponse.of(member);
    }

}
