package com.challenge.domain.challengeRecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRecordRepository extends JpaRepository<ChallengeRecord, Long> {

    List<ChallengeRecord> findAllByChallengeId(Long challengeId);

}
