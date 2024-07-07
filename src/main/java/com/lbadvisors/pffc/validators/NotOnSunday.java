package com.lbadvisors.pffc.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotOnSundayValidator.class)
@Documented
public @interface NotOnSunday {

    String message() default "Date cannot be on a Sunday";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
