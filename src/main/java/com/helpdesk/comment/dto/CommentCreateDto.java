package com.helpdesk.comment.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentCreateDto {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}