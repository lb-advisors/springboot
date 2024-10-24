package com.lbadvisors.pffc.validators;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DynamicDateRangeValidator implements ConstraintValidator<DynamicDateRange, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }

        // Zone for Pacific Time
        ZoneId pacificZone = ZoneId.of("America/Los_Angeles");

        // Get the current Pacific time
        ZonedDateTime nowPacific = ZonedDateTime.now(pacificZone);

        // Get the next 2 AM in Pacific Time
        ZonedDateTime next2amPacific = nowPacific.with(LocalTime.of(2, 0));
        if (nowPacific.isAfter(next2amPacific)) {
            // If it's already past 2 AM today, move to the next day 2 AM
            next2amPacific = next2amPacific.plusDays(1);
        }

        // Calculate 3 months from now at 2 AM
        ZonedDateTime threeMonthsLaterPacific = next2amPacific.plusMonths(3);

        // Convert the given LocalDate to ZonedDateTime at 2 AM in Pacific Time
        ZonedDateTime givenDateTime = ZonedDateTime.of(date, LocalTime.of(2, 0), pacificZone);

        // Check if the given date is between the next 2 AM and 3 months from now
        boolean isInRange = !givenDateTime.isBefore(next2amPacific) && !givenDateTime.isAfter(threeMonthsLaterPacific);

        return isInRange;
    }
}