package com.minhduc.careerconnect.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.minhduc.careerconnect.config.LoginFailureHandler;
import com.minhduc.careerconnect.config.PasswordConfig;
import com.minhduc.careerconnect.config.RoleBasedSuccessHandler;
import com.minhduc.careerconnect.config.SecurityConfig;
import com.minhduc.careerconnect.dto.StudentRegistrationForm;
import com.minhduc.careerconnect.repository.UserRepository;
import com.minhduc.careerconnect.service.StudentRegistrationService;

/**
 * Test form đăng ký và lỗi validate bằng MockMvc (task T4E796F).
 */
@WebMvcTest(controllers = RegistrationController.class)
@Import({SecurityConfig.class, PasswordConfig.class, RoleBasedSuccessHandler.class, LoginFailureHandler.class})
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRegistrationService registrationService;

    @MockitoBean
    private UserRepository userRepository; // dùng bởi validator @UniqueEmail

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void dangKy_deTrongTatCa_traLaiFormVoiLoiTungTruong() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("fullName", "")
                        .param("email", "")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("form", "fullName", "email", "password", "confirmPassword"));

        verify(registrationService, never()).register(any());
    }

    @Test
    void dangKy_matKhauNhapLaiKhongKhop_baoLoiDuoiONhapLai() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        mockMvc.perform(post("/register").with(csrf())
                        .param("fullName", "Test Sinh Viên")
                        .param("email", "test@example.com")
                        .param("password", "Matkhau123")
                        .param("confirmPassword", "Matkhau999"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("form", "confirmPassword", "PasswordsMatch"))
                // dữ liệu đã nhập được giữ lại
                .andExpect(model().attribute("form",
                        org.hamcrest.Matchers.hasProperty("email", org.hamcrest.Matchers.is("test@example.com"))));

        verify(registrationService, never()).register(any());
    }

    @Test
    void dangKy_emailDaDung_baoLoiDuoiOEmail() throws Exception {
        when(userRepository.existsByEmail("sv01@example.com")).thenReturn(true);

        mockMvc.perform(post("/register").with(csrf())
                        .param("fullName", "Ai Đó")
                        .param("email", "sv01@example.com")
                        .param("password", "Matkhau123")
                        .param("confirmPassword", "Matkhau123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("form", "email", "UniqueEmail"));
    }

    @Test
    void dangKy_matKhauKhongCoSo_baoLoi() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        mockMvc.perform(post("/register").with(csrf())
                        .param("fullName", "Test")
                        .param("email", "test@example.com")
                        .param("password", "chiCoChuThoi")
                        .param("confirmPassword", "chiCoChuThoi"))
                .andExpect(model().attributeHasFieldErrorCode("form", "password", "Pattern"));
    }

    @Test
    void dangKy_hopLe_chuyenToiDangNhapKemThongBao() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(registrationService.register(any(StudentRegistrationForm.class))).thenReturn(30L);

        mockMvc.perform(post("/register").with(csrf())
                        .param("fullName", "Test Sinh Viên")
                        .param("email", "test@example.com")
                        .param("password", "Matkhau123")
                        .param("confirmPassword", "Matkhau123"))
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(registrationService).register(any(StudentRegistrationForm.class));
    }

    @Test
    void dangKy_thieuCsrfToken_biChan() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Test")
                        .param("email", "test@example.com")
                        .param("password", "Matkhau123")
                        .param("confirmPassword", "Matkhau123"))
                .andExpect(status().isForbidden());

        verify(registrationService, never()).register(any());
    }
}
