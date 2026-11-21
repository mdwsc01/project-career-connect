package com.minhduc.careerconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.minhduc.careerconnect.repository.UserRepository;

/** Spring tự tiêm UserRepository vào validator này khi kiểm tra form trong controller. */
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // để @NotBlank báo lỗi trống
        }
        return !userRepository.existsByEmail(email.trim().toLowerCase());
    }
}
