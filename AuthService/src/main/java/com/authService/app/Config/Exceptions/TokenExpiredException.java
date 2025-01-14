package com.authService.app.Config.Exceptions;

public class TokenExpiredException extends Exception{

    public TokenExpiredException(String message) {
        super(message);

    }
}
