package com.challenge.domain.challenge;

import com.challenge.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT c FROM Challenge c " +
            "WHERE c.member = :member " +
            "AND c.isDeleted = false " +
            "AND c.startDateTime <= :currentDateTime " +
            "AND c.endDateTime >= :currentDateTime")
    List<Challenge> findChallengesBy(Member member, LocalDateTime currentDateTime);

}
