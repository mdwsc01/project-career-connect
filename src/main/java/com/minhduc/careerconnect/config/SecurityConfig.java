package com.minhduc.careerconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình bảo mật.
 * CSRF được bật mặc định cho mọi form POST (không gọi csrf().disable());
 * form Thymeleaf dùng th:action sẽ tự chèn CSRF token.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int REMEMBER_ME_SECONDS = 14 * 24 * 60 * 60; // 14 ngày

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UserDetailsService userDetailsService,
                                                   RoleBasedSuccessHandler successHandler,
                                                   LoginFailureHandler failureHandler,
                                                   @Value("${app.security.remember-me-key}") String rememberMeKey)
            throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password",
                        "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/partner/**").hasRole("PARTNER")
                .requestMatchers("/me/**").hasRole("STUDENT")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll())
            .rememberMe(remember -> remember
                .key(rememberMeKey)
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(REMEMBER_ME_SECONDS)
                .userDetailsService(userDetailsService))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll());
        return http.build();
    }
}
