package com.minhduc.careerconnect.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.minhduc.careerconnect.config.LoginFailureHandler;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/"; // đã đăng nhập thì không cần vào trang đăng nhập
        }
        Object error = session.getAttribute(LoginFailureHandler.SESSION_KEY);
        if (error != null) {
            model.addAttribute("loginError", error);
            session.removeAttribute(LoginFailureHandler.SESSION_KEY);
        }
        return "auth/login";
    }
}
