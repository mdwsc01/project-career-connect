package com.minhduc.careerconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Trang chủ riêng của từng vai trò, nơi chuyển tới sau khi đăng nhập.
 */
@Controller
public class RoleHomeController {

    @GetMapping("/me")
    public String studentHome() {
        return "me/home";
    }

    @GetMapping("/partner")
    public String partnerHome() {
        return "partner/home";
    }

    @GetMapping("/admin")
    public String adminHome() {
        return "admin/home";
    }
}
