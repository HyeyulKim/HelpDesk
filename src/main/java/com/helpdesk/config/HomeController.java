package com.helpdesk.config;

import com.helpdesk.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 로그인 사용자는 문의함으로, 비로그인 방문자는 로그인 화면으로 바로 이동
        if (userDetails != null) {
            return "redirect:/requests";
        }
        return "redirect:/user/login";
    }
}