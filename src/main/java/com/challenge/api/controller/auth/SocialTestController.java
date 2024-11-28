package com.challenge.api.controller.auth;

import com.challenge.api.service.auth.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SocialTestController {

    private final KakaoApiService kakaoApiService;

    @Value("${social.kakao.apikey}")
    private String kakaoApiKey;

    @Value("${social.kakao.redirect_uri}")
    private String kakaoRedirectUri;

    @GetMapping("/kakaoLoginPage")
    public String loginForm(Model model) {
        model.addAttribute("kakaoApiServiceKey", kakaoApiKey);
        model.addAttribute("redirectUri", kakaoRedirectUri);
        return "login";
    }

    @RequestMapping("/login/oauth2/code/kakao")
    public String kakaoLogin(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        // 1. redirectUri의 쿼리파라미터로 넘어온 code를 받음
        // 2. 토큰 받기
        String accessToken = kakaoApiService.getAccessToken(code);
        redirectAttributes.addFlashAttribute("accessToken", accessToken);

        return "redirect:/accessToken";
    }

    @GetMapping("/accessToken")
    public String accessTokenPage(Model model) {
        return "accessToken";
    }

}
