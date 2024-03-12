package ru.practicum.mainservice.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UpdateRequestStatusValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface RejectedOrConfirmed {
    String message() default "Status must have status CONFIRMED or REJECTED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
