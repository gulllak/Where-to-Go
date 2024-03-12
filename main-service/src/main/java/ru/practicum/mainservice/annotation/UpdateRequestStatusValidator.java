package ru.practicum.mainservice.annotation;

import ru.practicum.mainservice.model.RequestStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdateRequestStatusValidator implements ConstraintValidator<RejectedOrConfirmed, RequestStatus> {
    @Override
    public boolean isValid(RequestStatus status, ConstraintValidatorContext constraintValidatorContext) {
        return status == RequestStatus.CONFIRMED || status == RequestStatus.REJECTED;
    }
}
