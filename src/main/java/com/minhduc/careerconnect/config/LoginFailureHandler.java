package com.minhduc.careerconnect.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * Xử lý đăng nhập thất bại: lưu thông báo vào session rồi quay về /login?error.
 * Tài khoản bị khoá: chỉ báo lý do khoá khi người dùng nhập ĐÚNG mật khẩu,
 * để người lạ không dò được tình trạng tài khoản của người khác.
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    public static final String SESSION_KEY = "LOGIN_ERROR_MESSAGE";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public LoginFailureHandler(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String message = "Email hoặc mật khẩu không đúng.";

        if (exception instanceof LockedException) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if (email != null && password != null) {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                        "SELECT password_hash, locked_reason FROM users WHERE email = ?", email);
                if (!rows.isEmpty()
                        && passwordEncoder.matches(password, (String) rows.get(0).get("password_hash"))) {
                    Object reason = rows.get(0).get("locked_reason");
                    message = "Tài khoản của bạn đã bị khoá."
                            + (reason != null ? " Lý do: " + reason + "." : "")
                            + " Vui lòng liên hệ quản trị viên.";
                }
            }
        }

        request.getSession().setAttribute(SESSION_KEY, message);
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}
