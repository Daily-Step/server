package com.challenge.api.service.member.response;

import com.challenge.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageResponse {

    private final String profileImg;
    private final String nickname;
    private final Long ongoingCount;
    private final Long succeedCount;
    private final Long totalCount;

    @Builder
    public MyPageResponse(String profileImg, String nickname, Long ongoingCount, Long succeedCount, Long totalCount) {
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.ongoingCount = ongoingCount;
        this.succeedCount = succeedCount;
        this.totalCount = totalCount;
    }

    public static MyPageResponse of(Member member, Long ongoing, Long succeed, Long total) {
        return MyPageResponse.builder()
                .profileImg(member.getProfileImg())
                .nickname(member.getNickname())
                .ongoingCount(ongoing)
                .succeedCount(succeed)
                .totalCount(total)
                .build();
    }

}
