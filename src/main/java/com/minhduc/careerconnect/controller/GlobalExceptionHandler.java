package com.minhduc.careerconnect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;

/**
 * Xử lý lỗi toàn cục cho mọi controller (task T4E7963).
 *
 * - BusinessException          -> quay lại trang trước + thông báo flash màu đỏ
 * - ResourceNotFoundException  -> trang 404
 * - AccessDeniedException      -> trang 403 (chưa đăng nhập thì chuyển tới trang đăng nhập)
 *
 * Các lỗi còn lại (lỗi hệ thống, URL không tồn tại, lỗi chặn ở tầng Security) do Spring Boot
 * chuyển tới /error và hiển thị bằng templates/error/{mã lỗi}.html, không lộ stack trace.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        log.warn("Lỗi nghiệp vụ tại {}: {}", request.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + safeRefererPath(request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, HttpServletRequest request,
                                 HttpServletResponse response, Model model) {
        log.info("Không tìm thấy dữ liệu tại {}: {}", request.getRequestURI(), ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, HttpServletRequest request,
                                     HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            // Chưa đăng nhập: ném lại để Spring Security chuyển tới trang đăng nhập
            throw ex;
        }
        log.warn("Từ chối truy cập {} của {}", request.getRequestURI(), auth.getName());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return "error/403";
    }

    /** Chỉ quay lại trang trước nếu trang đó thuộc chính website này, tránh chuyển hướng ra ngoài. */
    private String safeRefererPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        if (referer != null && referer.startsWith(base)) {
            String path = referer.substring(base.length());
            return path.isEmpty() ? "/" : path;
        }
        return "/";
    }
}
