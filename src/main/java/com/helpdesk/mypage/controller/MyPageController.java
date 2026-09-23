package com.helpdesk.mypage.controller;

import com.helpdesk.request.service.RequestService;
import com.helpdesk.security.CustomUserDetails;
import com.helpdesk.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final RequestService requestService;

    @GetMapping("/mypage")
    public String myPage(
            @RequestParam(defaultValue = "1") int myPage,
            @RequestParam(defaultValue = "1") int assignedPage,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Long userId = userDetails.getUser().getUserId();
        Role role = userDetails.getUser().getRole();

        model.addAttribute("user", userDetails.getUser());

        // "내가 등록한 문의" 섹션 페이징
        model.addAttribute("myRequests", requestService.getMyRequests(userId, myPage));
        model.addAttribute("myPage", myPage);
        model.addAttribute("myTotalPages", Math.max(requestService.getMyRequestsTotalPages(userId), 1));

        // "내가 담당하는 문의" 섹션은 AGENT에게만 의미가 있음
        boolean isAgent = (role == Role.AGENT);
        model.addAttribute("isAgentOrAdmin", isAgent);

        if (isAgent) {
            model.addAttribute("assignedRequests", requestService.getAssignedRequests(userId, assignedPage));
            model.addAttribute("assignedPage", assignedPage);
            model.addAttribute("assignedTotalPages", Math.max(requestService.getAssignedRequestsTotalPages(userId), 1));
        } else {
            model.addAttribute("assignedRequests", Collections.emptyList());
            model.addAttribute("assignedPage", 1);
            model.addAttribute("assignedTotalPages", 1);
        }

        return "mypage/index";
    }
}