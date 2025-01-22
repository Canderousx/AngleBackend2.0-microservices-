package com.authService.app.Config.Exceptions;

public class UnknownRefreshTokenException extends Exception{

    public UnknownRefreshTokenException(String message) {
        super(message);
    }
}
