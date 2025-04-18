package com.backend.controller.admin.validation;

import com.backend.controller.admin.param.AddUserParam;
import com.backend.service.user.UserReadService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, AddUserParam> {

    private final UserReadService userReadService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AddUserParam param, ConstraintValidatorContext constraintValidatorContext) {
        return userReadService.findByEmail(param.getEmail(), null) == null;
    }

}