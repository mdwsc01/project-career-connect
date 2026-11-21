package com.minhduc.careerconnect.controller;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.dto.StudentRegistrationForm;
import com.minhduc.careerconnect.service.StudentRegistrationService;

@Controller
public class RegistrationController {

    private final StudentRegistrationService registrationService;

    public RegistrationController(StudentRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        if (isLoggedIn()) {
            return "redirect:/";
        }
        model.addAttribute("form", new StudentRegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") StudentRegistrationForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Trả lại đúng trang form: lỗi hiện dưới từng ô, dữ liệu đã nhập vẫn còn
            return "auth/register";
        }
        registrationService.register(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Đăng ký thành công! Hãy đăng nhập để hoàn thiện hồ sơ của bạn.");
        return "redirect:/login";
    }

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
}
