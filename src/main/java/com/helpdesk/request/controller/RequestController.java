package com.helpdesk.request.controller;

import com.helpdesk.attachment.service.AttachmentService;
import com.helpdesk.comment.dto.CommentCreateDto;
import com.helpdesk.comment.service.CommentService;
import com.helpdesk.request.domain.RequestPriority;
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
    private final AttachmentService attachmentService;

    @GetMapping
    public String list( //문의 목록
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestPriority priority,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        List<RequestListItemDto> requests = requestService.getRequests(status, priority, keyword, page);
        int totalPages = Math.max(requestService.getTotalPages(status, priority, keyword), 1);

        //Model은 Controller가 View(Thymeleaf)에게 데이터를 전달하는 상자
        //여기 담긴 키("requests", "status" 등)가 그대로 list.html에서 ${requests}, ${status}로 꺼내짐
        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        return "request/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) { //등록 폼 화면
        model.addAttribute("requestCreateDto", new RequestCreateDto());
        return "request/form";
    }

    @PostMapping
    public String create( //등록 처리
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
        return "redirect:/requests";//문자열 앞에 redirect:가 붙으면 뷰 렌더링이 아니라 새 요청을 보내라는 뜻
        // 등록 후 새로고침해도 중복 제출 안 되게 하는 관례(POST-Redirect-GET 패턴)
    }

    @GetMapping("/{requestId}")
    public String detail( //상세 조회
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        RequestDetailDto request = requestService.getRequestDetail(requestId);
        model.addAttribute("request", request);
        model.addAttribute("currentUserId", userDetails.getUser().getUserId());
        model.addAttribute("currentUserRole", userDetails.getUser().getRole());
        model.addAttribute("currentUserName", userDetails.getUser().getName());
        model.addAttribute("comments", commentService.getComments(requestId));
        model.addAttribute("commentCreateDto", new CommentCreateDto());
        model.addAttribute("attachments", attachmentService.getAttachments(requestId));
        return "request/detail";
    }

    @PostMapping("/{requestId}/status")
    public String advanceStatus(//상태 변경
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
    public String editForm( //수정 폼 화면
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
    public String edit( //수정 처리
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
    public String delete( // 삭제
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