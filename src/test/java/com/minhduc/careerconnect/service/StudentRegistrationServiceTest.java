package com.minhduc.careerconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.minhduc.careerconnect.dto.StudentRegistrationForm;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.UserRepository;

/**
 * Unit test đăng ký sinh viên (task T4E796F).
 */
@ExtendWith(MockitoExtension.class)
class StudentRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentRegistrationService service;

    private StudentRegistrationForm form(String name, String email) {
        StudentRegistrationForm form = new StudentRegistrationForm();
        form.setFullName(name);
        form.setEmail(email);
        form.setPassword("Matkhau123");
        form.setConfirmPassword("Matkhau123");
        return form;
    }

    @Test
    void register_thanhCong_maHoaMatKhauVaTaoHoSoTrong() {
        when(userRepository.existsByEmail("an@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Matkhau123")).thenReturn("HASHED");
        when(userRepository.insert("an@example.com", "HASHED", "Nguyễn Văn An", "STUDENT")).thenReturn(10L);

        long id = service.register(form("  Nguyễn Văn An ", "  An@Example.com "));

        assertThat(id).isEqualTo(10L);
        // email được chuẩn hoá chữ thường, mật khẩu lưu dạng mã hoá
        verify(userRepository).insert("an@example.com", "HASHED", "Nguyễn Văn An", "STUDENT");
        verify(profileRepository).insertEmptyProfile(10L, "nguyen-van-an-10");
    }

    @Test
    void register_emailDaTonTai_nemLoiVaKhongLuu() {
        when(userRepository.existsByEmail("sv01@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(form("Ai Đó", "sv01@example.com")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã được sử dụng");

        verify(userRepository, never()).insert(anyString(), anyString(), anyString(), anyString());
        verify(profileRepository, never()).insertEmptyProfile(anyLong(), anyString());
    }

    @Test
    void toSlug_boDauTiengVietVaKyTuDacBiet() {
        assertThat(StudentRegistrationService.toSlug("Đặng Quang Huy")).isEqualTo("dang-quang-huy");
        assertThat(StudentRegistrationService.toSlug("  Lê   Thị!!  Mai ")).isEqualTo("le-thi-mai");
        assertThat(StudentRegistrationService.toSlug("###")).isEqualTo("ho-so");
    }
}
