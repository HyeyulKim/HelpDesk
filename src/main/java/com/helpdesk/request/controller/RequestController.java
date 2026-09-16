package com.helpdesk.request.controller;

import com.helpdesk.comment.dto.CommentCreateDto;
import com.helpdesk.comment.service.CommentService;
import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.request.dto.RequestCreateDto;
import com.helpdesk.request.dto.RequestDetailDto;
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
    private final CommentService commentService;

    @GetMapping
    public String list(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        List<RequestListItemDto> requests = requestService.getRequests(status, page);
        int totalPages = Math.max(requestService.getTotalPages(status), 1);

        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        return "request/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("requestCreateDto", new RequestCreateDto());
        return "request/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute RequestCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "request/form";
        }

        Long requesterId = userDetails.getUser().getUserId();
        requestService.createRequest(requesterId, dto);
        return "redirect:/requests";
    }

    @GetMapping("/{requestId}")
    public String detail(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        RequestDetailDto request = requestService.getRequestDetail(requestId);
        model.addAttribute("request", request);
        model.addAttribute("currentUserId", userDetails.getUser().getUserId());
        model.addAttribute("currentUserRole", userDetails.getUser().getRole());
        model.addAttribute("comments", commentService.getComments(requestId));
        model.addAttribute("commentCreateDto", new CommentCreateDto());
        return "request/detail";
    }

    @PostMapping("/{requestId}/status")
    public String advanceStatus(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        requestService.advanceStatus(
                requestId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getRole()
        );
        return "redirect:/requests/" + requestId;
    }

    @GetMapping("/{requestId}/edit")
    public String editForm(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        RequestDetailDto request = requestService.getRequestForEdit(requestId, userDetails.getUser().getUserId());

        RequestCreateDto dto = new RequestCreateDto();
        dto.setTitle(request.getTitle());
        dto.setContent(request.getContent());
        dto.setPriority(request.getPriority());

        model.addAttribute("requestCreateDto", dto);
        model.addAttribute("requestId", requestId);
        return "request/edit";
    }

    @PostMapping("/{requestId}/edit")
    public String edit(
            @PathVariable Long requestId,
            @Valid @ModelAttribute RequestCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("requestId", requestId);
            return "request/edit";
        }

        requestService.updateRequest(requestId, userDetails.getUser().getUserId(), dto);
        return "redirect:/requests/" + requestId;
    }

    @PostMapping("/{requestId}/delete")
    public String delete(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        requestService.deleteRequest(
                requestId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getRole()
        );
        return "redirect:/requests";
    }
}