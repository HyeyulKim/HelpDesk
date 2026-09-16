package com.helpdesk.comment.controller;

import com.helpdesk.comment.dto.CommentCreateDto;
import com.helpdesk.comment.service.CommentService;
import com.helpdesk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/requests/{requestId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String create(
            @PathVariable Long requestId,
            @Valid @ModelAttribute CommentCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/requests/" + requestId;
        }

        commentService.createComment(
                requestId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getRole(),
                dto
        );

        return "redirect:/requests/" + requestId;
    }

    @PostMapping("/{commentId}/edit")
    public String edit(
            @PathVariable Long requestId,
            @PathVariable Long commentId,
            @Valid @ModelAttribute CommentCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/requests/" + requestId;
        }

        commentService.updateComment(commentId, userDetails.getUser().getUserId(), dto);
        return "redirect:/requests/" + requestId;
    }

    @PostMapping("/{commentId}/delete")
    public String delete(
            @PathVariable Long requestId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.deleteComment(
                commentId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getRole()
        );
        return "redirect:/requests/" + requestId;
    }
}