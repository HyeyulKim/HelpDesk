package com.helpdesk.mypage.controller;

import com.helpdesk.request.service.RequestService;
import com.helpdesk.security.CustomUserDetails;
import com.helpdesk.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final RequestService requestService;

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUser().getUserId();
        Role role = userDetails.getUser().getRole();

        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("myRequests", requestService.getMyRequests(userId));

        boolean isAgentOrAdmin = (role == Role.AGENT || role == Role.ADMIN);
        model.addAttribute("isAgentOrAdmin", isAgentOrAdmin);
        model.addAttribute(
                "assignedRequests",
                isAgentOrAdmin ? requestService.getAssignedRequests(userId) : Collections.emptyList()
        );

        return "mypage/index";
    }
}