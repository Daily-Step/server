package com.challenge.domain.challenge;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    boolean existsByMemberIdAndTitle(Long memberId, String title);

}
