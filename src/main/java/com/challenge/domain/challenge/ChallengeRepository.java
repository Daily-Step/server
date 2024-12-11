package com.challenge.domain.challenge;

import com.challenge.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    boolean existsByMemberAndId(Member member, Long challengeId);

}
