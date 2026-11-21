package com.minhduc.careerconnect.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Nạp tài khoản từ bảng users để Spring Security kiểm tra đăng nhập.
 * Đăng nhập bằng email; vai trò STUDENT/PARTNER/ADMIN thành quyền ROLE_STUDENT/...;
 * is_locked = true thì tài khoản bị khoá (Spring ném LockedException).
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public AppUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<UserDetails> users = jdbcTemplate.query(
                "SELECT email, password_hash, role, is_locked FROM users WHERE email = ?",
                (rs, rowNum) -> User.withUsername(rs.getString("email"))
                        .password(rs.getString("password_hash"))
                        .roles(rs.getString("role"))
                        .accountLocked(rs.getBoolean("is_locked"))
                        .build(),
                email);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản");
        }
        return users.get(0);
    }
}
