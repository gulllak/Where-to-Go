package ru.practicum.mainservice.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class FutureLocalDateTimeValidator implements ConstraintValidator<StartTimeChecker, LocalDateTime> {
    private long hours;

    @Override
    public void initialize(StartTimeChecker constraintAnnotation) {
        this.hours = constraintAnnotation.hours();
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        return localDateTime != null && localDateTime.isAfter(LocalDateTime.now().plusHours(hours));
    }
}
