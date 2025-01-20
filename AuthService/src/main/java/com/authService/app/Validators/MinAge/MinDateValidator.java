package com.authService.app.Validators.MinAge;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class MinDateValidator implements ConstraintValidator<MinDate, Date> {

    private int yearsAccepted;
    @Override
    public void initialize(MinDate constraintAnnotation) {
        this.yearsAccepted = constraintAnnotation.yearsAccepted();
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if(date == null){
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate toCompare = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int yearsBetween = Period.between(toCompare, today).getYears();

        return yearsBetween >= yearsAccepted;

    }
}
