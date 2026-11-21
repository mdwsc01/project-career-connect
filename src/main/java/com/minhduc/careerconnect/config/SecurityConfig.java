package com.minhduc.careerconnect.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/candidate/**").hasRole("CANDIDATE")
                .requestMatchers("/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/admin/users/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasAnyRole("MENTOR", "ADMIN")
                .anyRequest().authenticated())
            // Tạm dùng trang đăng nhập mặc định của Spring; task đăng nhập sẽ thay bằng trang riêng
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/?logout")
                .permitAll());
        return http.build();
    }

    /** Đọc tài khoản từ bảng users: đăng nhập bằng email, vai trò -> ROLE_xxx, bị khoá thì không đăng nhập được. */
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcDaoImpl service = new JdbcDaoImpl();
        service.setDataSource(dataSource);
        service.setUsersByUsernameQuery(
                "SELECT email, password_hash, NOT is_locked FROM users WHERE email = ?");
        service.setAuthoritiesByUsernameQuery(
                "SELECT email, CONCAT('ROLE_', role) FROM users WHERE email = ?");
        return service;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
