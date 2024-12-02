package com.challenge.api.service.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChallengeResponse createChallenge(Long memberId, ChallengeCreateServiceRequest request,
                                             LocalDateTime startDateTime) {


        return null;
    }

    public List<ChallengeResponse> getChallenges(Long memberId, LocalDateTime currentDateTime) {
        List<Challenge> challenges = challengeRepository.findChallengesBy(memberId, currentDateTime);

        return challenges.stream()
                .map(ChallengeResponse::of)
                .toList();
    }

}
