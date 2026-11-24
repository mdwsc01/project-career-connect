package com.minhduc.careerconnect.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.startField = annotation.start();
        this.endField = annotation.end();
    }

    @Override
    public boolean isValid(Object form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }
        BeanWrapperImpl wrapper = new BeanWrapperImpl(form);
        Object start = wrapper.getPropertyValue(startField);
        Object end = wrapper.getPropertyValue(endField);
        if (!(start instanceof LocalDate s) || !(end instanceof LocalDate e) || !s.isAfter(e)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(endField)
                .addConstraintViolation();
        return false;
    }
}
