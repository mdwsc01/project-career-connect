package com.minhduc.careerconnect.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;

/**
 * Trang thử lỗi, CHỈ chạy ở profile dev (task T4E7963). Cần đăng nhập.
 * /demo/success        -> thông báo flash thành công
 * /demo/business-error -> lỗi nghiệp vụ, thông báo flash màu đỏ
 * /demo/not-found      -> trang 404
 * /demo/server-error   -> trang 500
 */
@Controller
@Profile("dev")
@RequestMapping("/demo")
public class DevErrorDemoController {

    @GetMapping("/success")
    public String success(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Thao tác thành công (thông báo thử).");
        return "redirect:/";
    }

    @GetMapping("/business-error")
    public String businessError() {
        throw new BusinessException("Hồ sơ chưa đủ thông tin để gửi duyệt (lỗi thử).");
    }

    @GetMapping("/not-found")
    public String notFound() {
        throw new ResourceNotFoundException("Hồ sơ", 999);
    }

    @GetMapping("/server-error")
    public String serverError() {
        throw new IllegalStateException("Lỗi hệ thống giả lập - nội dung này không được hiện ra cho người dùng");
    }
}
