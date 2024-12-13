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
    private final long jobId;
    private final String job;
    private final int jobYearId;

    @Builder
    private MemberInfoResponse(String nickname, String birth, Gender gender, long jobId, String job, int jobYearId) {
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.jobId = jobId;
        this.job = job;
        this.jobYearId = jobYearId;
    }

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .nickname(member.getNickname())
                .birth(DateUtils.toDayString(member.getBirth()))
                .gender(member.getGender())
                .jobId(member.getJob().getId())
                .job(member.getJob().getDescription())
                .jobYearId(member.getJobYear().getId())
                .build();
    }

}
