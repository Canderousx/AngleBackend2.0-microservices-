package com.authService.app.Validators.MinAge;


import org.springframework.messaging.handler.annotation.Payload;

public @interface MinDate {
    String message() default "You must be at least {value} years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int yearsAccepted();
}
