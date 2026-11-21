package com.minhduc.careerconnect.service;

import java.text.Normalizer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.StudentRegistrationForm;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.UserRepository;

/**
 * Đăng ký tài khoản sinh viên (task T4E7965).
 */
@Service
public class StudentRegistrationService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentRegistrationService(UserRepository userRepository,
                                      ProfileRepository profileRepository,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Tạo tài khoản STUDENT (mật khẩu mã hoá BCrypt) và một hồ sơ trống ở trạng thái riêng tư.
     * Hai bước nằm trong cùng một transaction: lỗi ở bước nào thì không bước nào được lưu.
     */
    @Transactional
    public long register(StudentRegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase();
        String fullName = form.getFullName().trim();

        // Kiểm tra lại ở tầng service phòng khi hai người đăng ký cùng email cùng lúc
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email này đã được sử dụng");
        }

        long userId = userRepository.insert(email, passwordEncoder.encode(form.getPassword()), fullName, "STUDENT");
        profileRepository.insertEmptyProfile(userId, toSlug(fullName) + "-" + userId);
        return userId;
    }

    /**
     * "Nguyễn Văn An" -> "nguyen-van-an"
     */
    static String toSlug(String text) {
        String s = text.replace('đ', 'd').replace('Đ', 'D');
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }
        return s.isEmpty() ? "ho-so" : s;
    }
}
