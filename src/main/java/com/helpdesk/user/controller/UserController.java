package com.helpdesk.user.controller;

import com.helpdesk.user.dto.SignupRequestDto;
import com.helpdesk.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequestDto", new SignupRequestDto());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequestDto dto,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }
        try {
            userService.signup(dto);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/signup";
        }
        return "redirect:/user/login?signupSuccess=true";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }
}
