package com.lbadvisors.pffc.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class NotOnSundayValidator implements ConstraintValidator<NotOnSunday, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || value.getDayOfWeek() != DayOfWeek.SUNDAY;
    }
}
