package com.helpdesk.comment.service;

import com.helpdesk.comment.domain.Comment;
import com.helpdesk.comment.dto.CommentCreateDto;
import com.helpdesk.comment.dto.CommentDto;
import com.helpdesk.comment.mapper.CommentMapper;
import com.helpdesk.request.dto.RequestDetailDto;
import com.helpdesk.request.service.RequestService;
import com.helpdesk.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final RequestService requestService;

    public List<CommentDto> getComments(Long requestId) {
        return commentMapper.findByRequestId(requestId);
    }

    private CommentDto getComment(Long commentId) {
        return commentMapper.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다. id=" + commentId));
    }

    @Transactional
    public void createComment(Long requestId, Long authorId, Role authorRole, CommentCreateDto dto) {
        RequestDetailDto request = requestService.getRequestDetail(requestId);

        boolean isRequester = Objects.equals(request.getRequesterId(), authorId);
        boolean isAssignee = Objects.equals(request.getAssigneeId(), authorId);
        boolean isAdmin = authorRole == Role.ADMIN;

        if (!isRequester && !isAssignee && !isAdmin) {
            throw new AccessDeniedException("이 문의의 요청자, 담당자 또는 관리자만 댓글을 작성할 수 있습니다.");
        }

        Comment comment = Comment.builder()
                .requestId(requestId)
                .authorId(authorId)
                .content(dto.getContent())
                .build();

        commentMapper.insertComment(comment);
    }

    @Transactional
    public void updateComment(Long commentId, Long currentUserId, CommentCreateDto dto) {
        CommentDto comment = getComment(commentId);

        if (!Objects.equals(comment.getAuthorId(), currentUserId)) {
            throw new AccessDeniedException("작성자 본인만 댓글을 수정할 수 있습니다.");
        }

        commentMapper.updateContent(commentId, dto.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId, Long currentUserId, Role currentUserRole) {
        CommentDto comment = getComment(commentId);

        boolean isAuthor = Objects.equals(comment.getAuthorId(), currentUserId);
        boolean isAdmin = currentUserRole == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("작성자 본인 또는 관리자만 댓글을 삭제할 수 있습니다.");
        }

        commentMapper.deleteById(commentId);
    }
}