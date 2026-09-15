package com.helpdesk.request.controller;

import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.request.dto.RequestCreateDto;
import com.helpdesk.request.dto.RequestListItemDto;
import com.helpdesk.request.service.RequestService;
import com.helpdesk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public String list(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) { //목록 화면
        List<RequestListItemDto> requests = requestService.getRequests(status, page);
        int totalPages = Math.max(requestService.getTotalPages(status), 1);

        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        return "request/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) { //빈 등록 폼 화면을 보여줌
        model.addAttribute("requestCreateDto", new RequestCreateDto());
        return "request/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute RequestCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails, //현재 로그인한 사용자 정보를 스프링 시큐리티가 자동으로 주입
            Model model
    ) { //등록 폼 제출 처리
        if (bindingResult.hasErrors()) {
            return "request/form";
        }

        Long requesterId = userDetails.getUser().getUserId(); //"누가 이 문의를 등록했는지"를 서버가 직접 결정
        requestService.createRequest(requesterId, dto);
        return "redirect:/requests";
    }
}