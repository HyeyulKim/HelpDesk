package com.helpdesk.attachment.mapper;

import com.helpdesk.attachment.domain.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AttachmentMapper {

    void insertAttachment(Attachment attachment);

    List<Attachment> findByRequestId(@Param("requestId") Long requestId);

    Optional<Attachment> findById(@Param("attachmentId") Long attachmentId);

    void deleteById(@Param("attachmentId") Long attachmentId);
}