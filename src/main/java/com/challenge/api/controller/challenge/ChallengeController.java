package com.challenge.api.controller.challenge;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.controller.challenge.request.ChallengeAchieveRequest;
import com.challenge.api.controller.challenge.request.ChallengeCancelRequest;
import com.challenge.api.controller.challenge.request.ChallengeCreateRequest;
import com.challenge.api.controller.challenge.request.ChallengeQueryRequest;
import com.challenge.api.controller.challenge.request.ChallengeUpdateRequest;
import com.challenge.api.service.challenge.ChallengeService;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * 입력 받은 날짜 기준 이전 2달간의 챌린지 목록 조회 (2달전 00:00:00 ~ 입력받은 날짜 23:59:59)
     */
    @GetMapping("/challenges")
    public ApiResponse<List<ChallengeResponse>> getChallenges(
            @AuthMember Member member,
            @RequestBody @Valid ChallengeQueryRequest request) {
        return ApiResponse.ok(challengeService.getChallenges(member, request.toServiceRequest()));
    }

    /**
     * 챌린지 생성
     */
    @PostMapping("/challenges")
    public ApiResponse<ChallengeResponse> createChallenge(
            @AuthMember Member member,
            @RequestBody @Valid ChallengeCreateRequest request) {
        LocalDateTime startDateTime = LocalDateTime.now();
        return ApiResponse.ok(challengeService.createChallenge(member, request.toServiceRequest(), startDateTime));
    }

    /**
     * 챌린지 달성
     */
    @PostMapping("/challenges/{challengeId}/achieve")
    public ApiResponse<ChallengeResponse> achieveChallenge(
            @AuthMember Member member,
            @PathVariable Long challengeId,
            @RequestBody @Valid ChallengeAchieveRequest request) {
        return ApiResponse.ok(challengeService.achieveChallenge(member, challengeId, request.toServiceRequest()));
    }

    /**
     * 챌린지 달성 취소
     */
    @PostMapping("/challenges/{challengeId}/cancel")
    public ApiResponse<ChallengeResponse> cancelChallenge(
            @AuthMember Member member,
            @PathVariable Long challengeId,
            @RequestBody @Valid ChallengeCancelRequest request) {
        return ApiResponse.ok(challengeService.cancelChallenge(member, challengeId, request.toServiceRequest()));
    }

    /**
     * 챌린지 수정
     */
    @PutMapping("/challenges/{challengeId}")
    public ApiResponse<ChallengeResponse> updateChallenge(
            @AuthMember Member member,
            @PathVariable Long challengeId,
            @RequestBody @Valid ChallengeUpdateRequest request) {
        return ApiResponse.ok(challengeService.updateChallenge(member, challengeId, request.toServiceRequest()));
    }

    /**
     * 챌린지 삭제
     */
    @DeleteMapping("/challenges/{challengeId}")
    public ApiResponse<Long> deleteChallenge(@AuthMember Member member, @PathVariable Long challengeId) {
        return ApiResponse.ok(challengeService.deleteChallenge(member, challengeId));
    }

}
