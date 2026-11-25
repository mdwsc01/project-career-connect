package com.minhduc.careerconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.minhduc.careerconnect.repository.PasswordResetCodeRepository;
import com.minhduc.careerconnect.repository.PasswordResetCodeRepository.ResetCode;
import com.minhduc.careerconnect.repository.UserRepository;
import com.minhduc.careerconnect.repository.UserRepository.UserCredential;
import com.minhduc.careerconnect.service.PasswordService.ResetOutcome;
import com.minhduc.careerconnect.service.PasswordService.ResetResult;

/**
 * Unit test đổi mật khẩu và quên mật khẩu bằng OTP (task T4E796F).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordServiceTest {

    private static final String EMAIL = "sv03@example.com";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetCodeRepository codeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService service;

    @BeforeEach
    void setUp() {
        when(userRepository.findCredentialByEmail(EMAIL))
                .thenReturn(Optional.of(new UserCredential(7L, EMAIL, "OLD_HASH")));
    }

    // ------------------------------------------------------------ đổi mật khẩu

    @Test
    void changePassword_matKhauHienTaiSai_traVeFalseVaKhongDoi() {
        when(passwordEncoder.matches("sai-mat-khau", "OLD_HASH")).thenReturn(false);

        boolean changed = service.changePassword(EMAIL, "sai-mat-khau", "Moi12345");

        assertThat(changed).isFalse();
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void changePassword_matKhauHienTaiDung_luuMatKhauMoiDaMaHoa() {
        when(passwordEncoder.matches("Demo@1234", "OLD_HASH")).thenReturn(true);
        when(passwordEncoder.encode("Moi12345")).thenReturn("NEW_HASH");

        boolean changed = service.changePassword(EMAIL, "Demo@1234", "Moi12345");

        assertThat(changed).isTrue();
        verify(userRepository).updatePassword(7L, "NEW_HASH");
    }

    // ------------------------------------------------------------ quên mật khẩu

    private void activeCode(int attempts, LocalDateTime expiresAt) {
        when(codeRepository.findLatestActive(7L))
                .thenReturn(Optional.of(new ResetCode(99L, "CODE_HASH", expiresAt, attempts)));
    }

    @Test
    void resetPassword_nhapSaiMa_tangSoLanVaBaoConLai() {
        activeCode(0, LocalDateTime.now().plusMinutes(5));
        when(passwordEncoder.matches("111111", "CODE_HASH")).thenReturn(false);

        ResetOutcome outcome = service.resetPassword(EMAIL, "111111", "Moi12345");

        assertThat(outcome.result()).isEqualTo(ResetResult.WRONG_CODE);
        assertThat(outcome.remainingAttempts()).isEqualTo(4);
        verify(codeRepository).updateAttempts(99L, 1);
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void resetPassword_saiLanThu5_huyMa() {
        activeCode(4, LocalDateTime.now().plusMinutes(5));
        when(passwordEncoder.matches("111111", "CODE_HASH")).thenReturn(false);

        ResetOutcome outcome = service.resetPassword(EMAIL, "111111", "Moi12345");

        assertThat(outcome.result()).isEqualTo(ResetResult.CANCELLED);
        verify(codeRepository).markCancelled(99L);
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void resetPassword_maHetHan_khongChoDoi() {
        activeCode(0, LocalDateTime.now().minusMinutes(1));

        ResetOutcome outcome = service.resetPassword(EMAIL, "123456", "Moi12345");

        assertThat(outcome.result()).isEqualTo(ResetResult.INVALID_OR_EXPIRED);
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void resetPassword_maDung_doiMatKhauVaDanhDauDaDung() {
        activeCode(2, LocalDateTime.now().plusMinutes(5));
        when(passwordEncoder.matches("123456", "CODE_HASH")).thenReturn(true);
        when(passwordEncoder.encode("Moi12345")).thenReturn("NEW_HASH");

        ResetOutcome outcome = service.resetPassword(EMAIL, "123456", "Moi12345");

        assertThat(outcome.result()).isEqualTo(ResetResult.SUCCESS);
        verify(userRepository).updatePassword(7L, "NEW_HASH");
        verify(codeRepository).markUsed(99L); // mã chỉ dùng một lần
    }
}
