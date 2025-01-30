package com.Notifications.app.Config.Exceptions;

public class TokenExpiredException extends RuntimeException{

    public TokenExpiredException(String message) {
        super(message);

    }
}
