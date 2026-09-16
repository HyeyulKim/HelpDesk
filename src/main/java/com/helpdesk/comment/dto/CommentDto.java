package com.helpdesk.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long authorId;      // 현재 로그인 사용자와 비교해 "본인 댓글" 여부 판단용
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
}