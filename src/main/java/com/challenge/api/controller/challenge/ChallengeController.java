package com.challenge.api.controller.challenge;

import com.challenge.api.ApiResponse;
import com.challenge.api.service.challenge.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/members/{memberId}/challenges")
    public ApiResponse<List<Map<String, Object>>> getChallenges(@PathVariable Long memberId) {
        return ApiResponse.ok(challengeService.getChallenges(memberId));
    }

}
