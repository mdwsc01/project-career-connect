package com.minhduc.careerconnect.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Đặt trên trường email: kiểm tra email chưa được dùng trong bảng users. */
@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {

    String message() default "Email này đã được sử dụng";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
