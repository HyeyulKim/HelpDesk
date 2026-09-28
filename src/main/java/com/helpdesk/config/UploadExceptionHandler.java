package com.helpdesk.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class UploadExceptionHandler {

    private static final Pattern ATTACHMENT_URI = Pattern.compile("^/requests/(\\d+)/attachments/?$");

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("uploadError",
                "첨부파일 용량을 초과했습니다. 파일 하나당 최대 10MB, 한 번에 최대 30MB까지 올릴 수 있습니다.");

        String path = request.getRequestURI().substring(request.getContextPath().length());
        Matcher matcher = ATTACHMENT_URI.matcher(path);
        if (matcher.matches()) {
            return "redirect:/requests/" + matcher.group(1);   // 상세 화면에서 올리다 초과
        }
        return "redirect:/requests/new";                        // 등록 화면에서 올리다 초과
    }
}