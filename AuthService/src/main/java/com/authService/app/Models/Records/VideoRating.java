package com.authService.app.Models.Records;

public record VideoRating(
        String videoId,
        String userEmail,
        boolean rating,
        boolean previouslyRated
) {
}