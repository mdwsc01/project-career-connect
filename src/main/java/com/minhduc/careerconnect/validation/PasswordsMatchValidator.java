package com.minhduc.careerconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.minhduc.careerconnect.dto.StudentRegistrationForm;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, StudentRegistrationForm> {

    @Override
    public boolean isValid(StudentRegistrationForm form, ConstraintValidatorContext context) {
        if (form == null || form.getPassword() == null || form.getConfirmPassword() == null) {
            return true; // để @NotBlank báo lỗi trống
        }
        if (form.getPassword().equals(form.getConfirmPassword())) {
            return true;
        }
        // Gắn lỗi vào ô "nhập lại mật khẩu" để hiện ngay dưới ô đó
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("confirmPassword")
                .addConstraintViolation();
        return false;
    }
}
