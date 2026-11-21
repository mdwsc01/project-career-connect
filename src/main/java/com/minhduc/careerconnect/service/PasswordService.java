package com.minhduc.careerconnect.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.repository.PasswordResetCodeRepository;
import com.minhduc.careerconnect.repository.PasswordResetCodeRepository.ResetCode;
import com.minhduc.careerconnect.repository.UserRepository;
import com.minhduc.careerconnect.repository.UserRepository.UserCredential;

/**
 * Đổi mật khẩu và quên mật khẩu bằng mã OTP (task T4E7966).
 */
@Service
public class PasswordService {

    private static final Logger log = LoggerFactory.getLogger(PasswordService.class);

    public static final int CODE_VALID_MINUTES = 10;
    public static final int MAX_WRONG_ATTEMPTS = 5;

    /**
     * Kết quả đặt lại mật khẩu. Trả về kết quả thay vì ném lỗi để số lần nhập sai vẫn được lưu.
     */
    public enum ResetResult {SUCCESS, INVALID_OR_EXPIRED, WRONG_CODE, CANCELLED}

    public record ResetOutcome(ResetResult result, int remainingAttempts) {
    }

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public PasswordService(UserRepository userRepository,
                           PasswordResetCodeRepository codeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------------------------------------------------------ đổi mật khẩu

    /**
     * @return false nếu mật khẩu hiện tại không đúng
     */
    @Transactional
    public boolean changePassword(String email, String currentPassword, String newPassword) {
        UserCredential user = userRepository.findCredentialByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập"));
        if (!passwordEncoder.matches(currentPassword, user.passwordHash())) {
            return false;
        }
        userRepository.updatePassword(user.id(), passwordEncoder.encode(newPassword));
        log.info("Tài khoản {} đã đổi mật khẩu", email);
        return true;
    }

    // ------------------------------------------------------------------ quên mật khẩu

    /**
     * Sinh mã OTP 6 số cho email (nếu email tồn tại), lưu dạng mã hoá, hết hạn sau 10 phút.
     * Thay cho gửi email: ghi mã ra log. Email không tồn tại thì không làm gì
     * (giao diện vẫn báo như nhau để không lộ email nào có trong hệ thống).
     */
    @Transactional
    public void requestResetCode(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        Optional<UserCredential> user = userRepository.findCredentialByEmail(email);
        if (user.isEmpty()) {
            log.info("Yêu cầu mã đặt lại mật khẩu cho email không tồn tại: {}", email);
            return;
        }
        long userId = user.get().id();
        codeRepository.cancelActiveCodes(userId);

        String code = String.format("%06d", random.nextInt(1_000_000));
        codeRepository.insert(userId, passwordEncoder.encode(code),
                LocalDateTime.now().plusMinutes(CODE_VALID_MINUTES));

        // Thay cho gửi email
        log.info("[OTP] Mã đặt lại mật khẩu cho {}: {} (hết hạn sau {} phút)", email, code, CODE_VALID_MINUTES);
    }

    /**
     * Kiểm tra mã và đặt mật khẩu mới. Mã chỉ dùng một lần; sai 5 lần thì mã bị huỷ.
     */
    @Transactional
    public ResetOutcome resetPassword(String rawEmail, String code, String newPassword) {
        String email = rawEmail.trim().toLowerCase();
        Optional<UserCredential> user = userRepository.findCredentialByEmail(email);
        if (user.isEmpty()) {
            return new ResetOutcome(ResetResult.INVALID_OR_EXPIRED, 0);
        }
        Optional<ResetCode> found = codeRepository.findLatestActive(user.get().id());
        if (found.isEmpty()) {
            return new ResetOutcome(ResetResult.INVALID_OR_EXPIRED, 0);
        }
        ResetCode resetCode = found.get();

        if (resetCode.expiresAt().isBefore(LocalDateTime.now())) {
            codeRepository.markCancelled(resetCode.id());
            return new ResetOutcome(ResetResult.INVALID_OR_EXPIRED, 0);
        }

        if (!passwordEncoder.matches(code, resetCode.codeHash())) {
            int attempts = resetCode.attempts() + 1;
            if (attempts >= MAX_WRONG_ATTEMPTS) {
                codeRepository.markCancelled(resetCode.id());
                log.warn("Mã đặt lại mật khẩu của {} bị huỷ do nhập sai {} lần", email, attempts);
                return new ResetOutcome(ResetResult.CANCELLED, 0);
            }
            codeRepository.updateAttempts(resetCode.id(), attempts);
            return new ResetOutcome(ResetResult.WRONG_CODE, MAX_WRONG_ATTEMPTS - attempts);
        }

        userRepository.updatePassword(user.get().id(), passwordEncoder.encode(newPassword));
        codeRepository.markUsed(resetCode.id());
        log.info("Tài khoản {} đã đặt lại mật khẩu bằng mã OTP", email);
        return new ResetOutcome(ResetResult.SUCCESS, 0);
    }
}
