package com.authService.app.Models.Records;

public record LoginRecord(
        String email,
        String password,

        String fingerprint
) {
}
