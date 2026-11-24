package com.minhduc.careerconnect.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Đặt trên class form: ngày bắt đầu không được sau ngày kết thúc.
 * Dùng lại được cho học vấn, dự án, chứng chỉ... Ngày kết thúc để trống thì bỏ qua (VD: đang học).
 */
@Documented
@Constraint(validatedBy = ValidDateRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

    String message() default "Ngày bắt đầu không được sau ngày kết thúc";

    /** Tên trường ngày bắt đầu. */
    String start() default "startDate";

    /** Tên trường ngày kết thúc (lỗi hiện dưới trường này). */
    String end() default "endDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
