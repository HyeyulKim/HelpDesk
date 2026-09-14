package com.helpdesk.attachment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    private Long attachmentId;
    private Long requestId;
    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
