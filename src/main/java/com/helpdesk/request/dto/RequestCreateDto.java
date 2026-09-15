package com.helpdesk.request.dto;

import com.helpdesk.request.domain.RequestPriority;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RequestCreateDto { //문의 등록 화면에서 사용자가 입력한 값을 담는 그릇

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private RequestPriority priority; // 미선택 시 서비스단에서 NORMAL로 기본 지정
}