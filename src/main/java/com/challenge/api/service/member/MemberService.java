package com.challenge.api.service.member;

import com.challenge.api.service.member.request.CheckNicknameServiceRequest;
import com.challenge.api.service.member.response.MemberInfoResponse;
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
     * 회원 정보 조회 메소드
     *
     * @param member
     * @return
     */
    public MemberInfoResponse getMemberInfo(Member member) {
        return MemberInfoResponse.of(member);
    }

}