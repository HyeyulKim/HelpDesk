package com.helpdesk.attachment.controller;

import com.helpdesk.attachment.domain.Attachment;
import com.helpdesk.attachment.service.AttachmentService;
import com.helpdesk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/requests/{requestId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public String upload(
            @PathVariable Long requestId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        attachmentService.uploadAttachments(
                requestId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getRole(),
                files
        );
        return "redirect:/requests/" + requestId;
    }

    @GetMapping("/{attachmentId}/download")
    @ResponseBody
    public ResponseEntity<Resource> download(
            @PathVariable Long requestId,
            @PathVariable Long attachmentId
    ) throws MalformedURLException {
        Attachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = new UrlResource(Paths.get(attachment.getFilePath()).toUri());

        String encodedFilename = java.net.URLEncoder.encode(attachment.getOriginalFilename(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    @PostMapping("/{attachmentId}/delete")
    public String delete(
            @PathVariable Long requestId,
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        attachmentService.deleteAttachment(
                attachmentId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getRole()
        );
        return "redirect:/requests/" + requestId;
    }
}