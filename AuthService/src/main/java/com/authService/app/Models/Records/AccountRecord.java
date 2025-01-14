package com.authService.app.Models.Records;

public record AccountRecord(
        String id,
        String username,
        String avatar,

        String email
) {
}
