package com.minhduc.careerconnect.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Đặt trên class form: kiểm tra hai trường mật khẩu giống nhau.
 * Mặc định so sánh "password" và "confirmPassword"; form khác có thể đổi tên trường, ví dụ
 * {@code @PasswordsMatch(password = "newPassword")}.
 */
@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatch {

    String message() default "Mật khẩu nhập lại không khớp";

    /** Tên trường mật khẩu. */
    String password() default "password";

    /** Tên trường nhập lại mật khẩu (lỗi sẽ hiện dưới trường này). */
    String confirm() default "confirmPassword";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
