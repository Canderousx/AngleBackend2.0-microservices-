package com.authService.app.Models.Records;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewPasswordRecord(

        @Size(min = 7,max = 15,message = "Password should contain at least 7 chars and 15 at maximum.")
        String password,

        @NotNull(message = "Token is missing.")
        String token
) {
}
