package com.challenge.api.service.member;


import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

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
