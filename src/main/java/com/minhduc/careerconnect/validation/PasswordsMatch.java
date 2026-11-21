package com.minhduc.careerconnect.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Đặt trên class form: kiểm tra password và confirmPassword giống nhau. */
@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatch {

    String message() default "Mật khẩu nhập lại không khớp";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
