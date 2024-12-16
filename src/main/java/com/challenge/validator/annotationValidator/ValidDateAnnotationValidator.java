package com.challenge.validator.annotationValidator;

import com.challenge.utils.date.DateUtils;
import com.challenge.validator.annotation.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidDateAnnotationValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            LocalDate localDate = DateUtils.toLocalDate(value);
            return !localDate.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

}
