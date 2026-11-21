package com.minhduc.careerconnect.config;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Đăng nhập thành công: chuyển tới trang chủ theo vai trò.
 */
@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        String target;
        if (roles.contains("ROLE_ADMIN")) {
            target = "/admin";
        } else if (roles.contains("ROLE_PARTNER")) {
            target = "/partner";
        } else if (roles.contains("ROLE_STUDENT")) {
            target = "/me";
        } else {
            target = "/";
        }
        request.getSession().removeAttribute(LoginFailureHandler.SESSION_KEY);
        response.sendRedirect(request.getContextPath() + target);
    }
}
