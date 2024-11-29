package com.challenge.api.service.challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;

}
