package com.authService.app.Models.Records;

public record AuthRecord(
        String authToken,
        String session
) {
}
