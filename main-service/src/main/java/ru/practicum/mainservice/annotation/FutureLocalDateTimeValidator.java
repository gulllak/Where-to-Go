package ru.practicum.mainservice.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class FutureLocalDateTimeValidator implements ConstraintValidator<AfterTwoHours, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        return localDateTime != null && localDateTime.isAfter(LocalDateTime.now().plusHours(2));
    }
}
