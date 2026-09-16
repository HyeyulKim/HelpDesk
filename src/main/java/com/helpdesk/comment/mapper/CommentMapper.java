package com.helpdesk.comment.mapper;

import com.helpdesk.comment.domain.Comment;
import com.helpdesk.comment.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {

    void insertComment(Comment comment);

    List<CommentDto> findByRequestId(@Param("requestId") Long requestId);

    Optional<CommentDto> findById(@Param("commentId") Long commentId);

    void updateContent(@Param("commentId") Long commentId, @Param("content") String content);

    void deleteById(@Param("commentId") Long commentId);
}