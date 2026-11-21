package com.minhduc.careerconnect.controller;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.dto.ChangePasswordForm;
import com.minhduc.careerconnect.dto.ForgotPasswordForm;
import com.minhduc.careerconnect.dto.ResetPasswordForm;
import com.minhduc.careerconnect.service.PasswordService;
import com.minhduc.careerconnect.service.PasswordService.ResetOutcome;

@Controller
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // ---------------------------------------------------------------- đổi mật khẩu (đã đăng nhập)

    @GetMapping("/account/password")
    public String changePasswordForm(Model model) {
        model.addAttribute("form", new ChangePasswordForm());
        return "account/change-password";
    }

    @PostMapping("/account/password")
    public String changePassword(@Valid @ModelAttribute("form") ChangePasswordForm form,
                                 BindingResult bindingResult, Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "account/change-password";
        }
        boolean changed = passwordService.changePassword(
                principal.getName(), form.getCurrentPassword(), form.getNewPassword());
        if (!changed) {
            bindingResult.rejectValue("currentPassword", "wrong", "Mật khẩu hiện tại không đúng");
            return "account/change-password";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công.");
        return "redirect:/account/password";
    }

    // ---------------------------------------------------------------- quên mật khẩu (chưa đăng nhập)

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("form", new ForgotPasswordForm());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestCode(@Valid @ModelAttribute("form") ForgotPasswordForm form,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        passwordService.requestResetCode(form.getEmail());
        // Thông báo giống nhau dù email có tồn tại hay không
        redirectAttributes.addFlashAttribute("infoMessage",
                "Nếu email có trong hệ thống, mã xác nhận 6 số đã được gửi. Mã có hiệu lực trong "
                        + PasswordService.CODE_VALID_MINUTES + " phút.");
        redirectAttributes.addAttribute("email", form.getEmail().trim().toLowerCase());
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(required = false) String email, Model model) {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setEmail(email);
        model.addAttribute("form", form);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("form") ResetPasswordForm form,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }
        ResetOutcome outcome = passwordService.resetPassword(
                form.getEmail(), form.getCode(), form.getNewPassword());
        switch (outcome.result()) {
            case SUCCESS -> {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đặt lại mật khẩu thành công. Hãy đăng nhập bằng mật khẩu mới.");
                return "redirect:/login";
            }
            case WRONG_CODE -> bindingResult.rejectValue("code", "wrong",
                    "Mã xác nhận không đúng. Bạn còn " + outcome.remainingAttempts() + " lần thử.");
            case CANCELLED -> bindingResult.rejectValue("code", "cancelled",
                    "Bạn đã nhập sai quá " + PasswordService.MAX_WRONG_ATTEMPTS
                            + " lần, mã đã bị huỷ. Vui lòng yêu cầu mã mới.");
            default -> bindingResult.rejectValue("code", "invalid",
                    "Mã không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu mã mới.");
        }
        return "auth/reset-password";
    }
}
