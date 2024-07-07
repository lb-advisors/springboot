package com.lbadvisors.pffc.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DynamicDateRangeValidator.class)
public @interface DynamicDateRange {
    String message() default "Date is out of valid range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}