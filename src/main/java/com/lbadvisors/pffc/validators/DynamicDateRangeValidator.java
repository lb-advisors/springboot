package com.lbadvisors.pffc.validators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DynamicDateRangeValidator implements ConstraintValidator<DynamicDateRange, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // Use @NotNull if null values should be invalid
        }
        try {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusMonths(3);
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}