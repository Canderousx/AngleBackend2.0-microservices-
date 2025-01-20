package com.authService.app.Models.Records;

import com.authService.app.Validators.MinAge.MinDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record NewUserRecord(

        @Size(min = 3, max = 15, message = "Username should contain at least 3 chars and 8 at maximum")
        String username,
        @Email(message = "Invalid email address.")
        String email,

        @Size(min = 7,max = 15,message = "Password should contain at least 7 chars and 15 at maximum")
        String password,


        @MinDate(yearsAccepted = 13)
        Date birthDate
) {
}
