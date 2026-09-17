package com.helpdesk.attachment.service;

import com.helpdesk.attachment.domain.Attachment;
import com.helpdesk.attachment.mapper.AttachmentMapper;
import com.helpdesk.request.dto.RequestDetailDto;
import com.helpdesk.request.service.RequestService;
import com.helpdesk.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentMapper attachmentMapper;
    private final RequestService requestService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<Attachment> getAttachments(Long requestId) {
        return attachmentMapper.findByRequestId(requestId);
    }

    public Attachment getAttachment(Long attachmentId) {
        return attachmentMapper.findById(attachmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 첨부파일입니다. id=" + attachmentId));
    }

    /**
     * 첨부파일 업로드는 그 문의의 요청자 본인 또는 관리자만 가능하다.
     * (담당자는 문의 처리에는 관여하지만 파일을 직접 첨부할 권한은 없음)
     */
    @Transactional
    public void uploadAttachments(Long requestId, Long currentUserId, Role currentUserRole, List<MultipartFile> files) {
        RequestDetailDto request = requestService.getRequestDetail(requestId);

        boolean isRequester = Objects.equals(request.getRequesterId(), currentUserId);
        boolean isAdmin = currentUserRole == Role.ADMIN;

        if (!isRequester && !isAdmin) {
            throw new AccessDeniedException("이 문의의 요청자 또는 관리자만 첨부파일을 업로드할 수 있습니다.");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue; // 파일을 선택하지 않은 input은 건너뜀
            }
            saveFile(requestId, file);
        }
    }

    private void saveFile(Long requestId, MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String originalFilename = Paths.get(Objects.requireNonNull(file.getOriginalFilename()))
                    .getFileName().toString(); // 경로 조작 방지, 파일명만 추출

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = uploadPath.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Attachment attachment = Attachment.builder()
                    .requestId(requestId)
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .filePath(targetPath.toString())
                    .fileSize(file.getSize())
                    .build();

            attachmentMapper.insertAttachment(attachment);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 중 오류가 발생했습니다: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * 삭제는 그 문의의 작성자 본인 또는 관리자만 가능하다.
     * (첨부파일 자체에 업로더 정보를 따로 저장하지 않으므로, 문의 소유권 기준으로 판단)
     */
    @Transactional
    public void deleteAttachment(Long attachmentId, Long currentUserId, Role currentUserRole) {
        Attachment attachment = getAttachment(attachmentId);
        RequestDetailDto request = requestService.getRequestDetail(attachment.getRequestId());

        boolean isOwner = Objects.equals(request.getRequesterId(), currentUserId);
        boolean isAdmin = currentUserRole == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("작성자 본인 또는 관리자만 첨부파일을 삭제할 수 있습니다.");
        }

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            throw new UncheckedIOException("파일 삭제 중 오류가 발생했습니다: " + attachment.getStoredFilename(), e);
        }

        attachmentMapper.deleteById(attachmentId);
    }
}