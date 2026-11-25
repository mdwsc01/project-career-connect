package com.minhduc.careerconnect.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.minhduc.careerconnect.config.LoginFailureHandler;
import com.minhduc.careerconnect.config.PasswordConfig;
import com.minhduc.careerconnect.config.RoleBasedSuccessHandler;
import com.minhduc.careerconnect.config.SecurityConfig;

/**
 * Test đăng nhập và phân quyền URL bằng MockMvc (task T4E796F).
 *
 * @WebMvcTest chỉ dựng tầng web, không cần MySQL; phần truy cập DB được thay bằng mock.
 */
@WebMvcTest(controllers = {AuthController.class, RoleHomeController.class})
@Import({SecurityConfig.class, PasswordConfig.class, RoleBasedSuccessHandler.class, LoginFailureHandler.class})
class SecurityAndLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JdbcTemplate jdbcTemplate; // dùng bởi GlobalModelAdvice và LoginFailureHandler

    // ------------------------------------------------------------ trang đăng nhập

    @Test
    void trangDangNhap_chuaDangNhap_moDuoc() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void dangNhap_dungMatKhau_chuyenVeTrangChuTheoVaiTro() throws Exception {
        when(userDetailsService.loadUserByUsername("sv@example.com")).thenReturn(
                User.withUsername("sv@example.com").password(passwordEncoder.encode("Matkhau123")).roles("STUDENT").build());

        mockMvc.perform(formLogin("/login").userParameter("email").user("sv@example.com").password("Matkhau123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/me"))
                .andExpect(authenticated().withRoles("STUDENT"));
    }

    @Test
    void dangNhap_adminDung_chuyenVeTrangQuanTri() throws Exception {
        when(userDetailsService.loadUserByUsername("admin@example.com")).thenReturn(
                User.withUsername("admin@example.com").password(passwordEncoder.encode("Matkhau123")).roles("ADMIN").build());

        mockMvc.perform(formLogin("/login").userParameter("email").user("admin@example.com").password("Matkhau123"))
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void dangNhap_saiMatKhau_quayLaiTrangDangNhapBaoLoi() throws Exception {
        when(userDetailsService.loadUserByUsername("sv@example.com")).thenReturn(
                User.withUsername("sv@example.com").password(passwordEncoder.encode("Matkhau123")).roles("STUDENT").build());

        mockMvc.perform(formLogin("/login").userParameter("email").user("sv@example.com").password("sai-mat-khau"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    // ------------------------------------------------------------ phân quyền URL

    @Test
    void trangSinhVien_chuaDangNhap_bichuyenToiDangNhap() throws Exception {
        mockMvc.perform(get("/me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "sv01@example.com", roles = "STUDENT")
    void trangQuanTri_sinhVienVao_bi403() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "doitac1@example.com", roles = "PARTNER")
    void trangSinhVien_doiTacVao_bi403() throws Exception {
        mockMvc.perform(get("/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void trangQuanTri_adminVao_moDuoc() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));
    }

    @Test
    @WithMockUser(username = "doitac1@example.com", roles = "PARTNER")
    void trangDoiTac_doiTacVao_moDuoc() throws Exception {
        mockMvc.perform(get("/partner"))
                .andExpect(status().isOk())
                .andExpect(view().name("partner/home"));
    }
}
