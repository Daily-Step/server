package com.challenge.domain.challenge;

import com.challenge.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // 해당 멤버에 대한 챌린지 존재 여부 조회
    boolean existsByMemberAndId(Member member, Long challengeId);

}
