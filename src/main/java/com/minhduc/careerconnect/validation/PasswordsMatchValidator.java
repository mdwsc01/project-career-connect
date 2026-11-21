package com.minhduc.careerconnect.validation;

import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {

    private String passwordField;
    private String confirmField;

    @Override
    public void initialize(PasswordsMatch annotation) {
        this.passwordField = annotation.password();
        this.confirmField = annotation.confirm();
    }

    @Override
    public boolean isValid(Object form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }
        BeanWrapperImpl wrapper = new BeanWrapperImpl(form);
        Object password = wrapper.getPropertyValue(passwordField);
        Object confirm = wrapper.getPropertyValue(confirmField);
        if (password == null || confirm == null || Objects.equals(password, confirm)) {
            return true; // trường trống do @NotBlank báo
        }
        // Gắn lỗi vào ô "nhập lại" để hiện ngay dưới ô đó
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(confirmField)
                .addConstraintViolation();
        return false;
    }
}
