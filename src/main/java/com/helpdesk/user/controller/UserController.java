package com.helpdesk.user.controller;

import com.helpdesk.emailverification.service.EmailVerificationService;
import com.helpdesk.user.dto.SignupRequestDto;
import com.helpdesk.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private static final String SESSION_VERIFIED_EMAIL = "verifiedEmail";

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/signup")
    public String signupForm(Model model, HttpSession session) {
        // 이메일 발송/인증 후 리다이렉트로 돌아왔다면, 그때 채워뒀던 입력값(inputDto)을 그대로 복원
        SignupRequestDto dto = model.containsAttribute("inputDto")
                ? (SignupRequestDto) model.getAttribute("inputDto")
                : new SignupRequestDto();
        model.addAttribute("signupRequestDto", dto);
        model.addAttribute("verifiedEmail", session.getAttribute(SESSION_VERIFIED_EMAIL));
        return "user/signup";
    }

    /**
     * 이메일 인증번호 발송. 이미 가입된 이메일이면 발송하지 않는다.
     * 폼 전체(@ModelAttribute)를 받아서, 리다이렉트 후에도 아이디/비밀번호/이름이 안 날아가게 그대로 되돌려준다.
     */
    @PostMapping("/signup/email/send")
    public String sendEmailCode(
            @ModelAttribute SignupRequestDto dto,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("inputDto", dto);

        if (!StringUtils.hasText(dto.getEmail())) {
            redirectAttributes.addFlashAttribute("emailSendError", "이메일을 먼저 입력해주세요.");
            return "redirect:/user/signup";
        }

        if (!userService.isEmailAvailable(dto.getEmail())) {
            redirectAttributes.addFlashAttribute("emailSendError", "이미 사용중인 이메일입니다.");
            return "redirect:/user/signup";
        }

        emailVerificationService.sendVerificationCode(dto.getEmail());
        redirectAttributes.addFlashAttribute("codeSent", true);
        return "redirect:/user/signup";
    }

    /**
     * 사용자가 입력한 인증번호 확인. 성공 시 세션에 인증된 이메일을 기록한다.
     * 이것도 마찬가지로 폼 전체를 받아서 입력값을 보존한다.
     */
    @PostMapping("/signup/email/verify")
    public String verifyEmailCode(
            @ModelAttribute SignupRequestDto dto,
            @RequestParam String code,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("inputDto", dto);

        if (!StringUtils.hasText(dto.getEmail())) {
            redirectAttributes.addFlashAttribute("verifyError", "이메일을 먼저 입력해주세요.");
            return "redirect:/user/signup";
        }

        boolean verified = emailVerificationService.verifyCode(dto.getEmail(), code);
        if (verified) {
            session.setAttribute(SESSION_VERIFIED_EMAIL, dto.getEmail());
            redirectAttributes.addFlashAttribute("emailVerifiedNow", true);
        } else {
            redirectAttributes.addFlashAttribute("verifyError", "인증번호가 올바르지 않거나 만료되었습니다.");
        }
        return "redirect:/user/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute SignupRequestDto dto,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("verifiedEmail", session.getAttribute(SESSION_VERIFIED_EMAIL));
            return "user/signup";
        }

        Object verifiedEmail = session.getAttribute(SESSION_VERIFIED_EMAIL);
        boolean emailVerified = dto.getEmail().equals(verifiedEmail);

        try {
            userService.signup(dto, emailVerified);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("verifiedEmail", verifiedEmail);
            return "user/signup";
        }

        session.removeAttribute(SESSION_VERIFIED_EMAIL);
        return "redirect:/user/login?signupSuccess=true";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }
}