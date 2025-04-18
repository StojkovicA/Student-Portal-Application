package com.backend.controller.admin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE, ElementType.TYPE_USE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {UniqueEmailValidator.class})
public @interface UniqueEmail {

    Class<?>[] groups() default {};

    String message() default "email:User with this email already exists";

    Class<? extends Payload>[] payload() default {};

}
