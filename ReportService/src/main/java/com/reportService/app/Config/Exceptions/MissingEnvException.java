package com.reportService.app.Config.Exceptions;

public class MissingEnvException extends RuntimeException{

    public MissingEnvException(String message) {
        super(message);
    }
}
