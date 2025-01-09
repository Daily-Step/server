package com.challenge.api.service.member.response;

import com.challenge.domain.member.Gender;
import com.challenge.domain.member.Member;
import com.challenge.utils.date.DateUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

    private final String nickname;
    private final String birth;
    private final Gender gender;
    private final Long jobId;
    private final String job;
    private final Integer jobYearId;

    @Builder
    private MemberInfoResponse(String nickname, String birth, Gender gender, Long jobId, String job,
                               Integer jobYearId) {
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.jobId = jobId;
        this.job = job;
        this.jobYearId = jobYearId;
    }

    public static MemberInfoResponse of(Member member) {
        Long jobId = member.getJob() == null ? null : member.getJob().getId();
        String job = member.getJob() == null ? null : member.getJob().getDescription();
        Integer jobYearId = member.getJobYear() == null ? null : member.getJobYear().getId();

        return MemberInfoResponse.builder()
                .nickname(member.getNickname())
                .birth(DateUtils.toDayString(member.getBirth()))
                .gender(member.getGender())
                .jobId(jobId)
                .job(job)
                .jobYearId(jobYearId)
                .build();
    }

}
