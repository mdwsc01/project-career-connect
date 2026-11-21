package com.minhduc.careerconnect.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Đưa họ tên người đang đăng nhập vào mọi trang (dùng ở header của layout).
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final JdbcTemplate jdbcTemplate;

    public GlobalModelAdvice(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @ModelAttribute("currentUserFullName")
    public String currentUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        try {
            List<String> names = jdbcTemplate.queryForList(
                    "SELECT full_name FROM users WHERE email = ?", String.class, auth.getName());
            return names.isEmpty() ? auth.getName() : names.get(0);
        } catch (DataAccessException ex) {
            // Không để lỗi DB làm hỏng luôn trang lỗi; hiện email thay cho họ tên
            return auth.getName();
        }
    }
}
