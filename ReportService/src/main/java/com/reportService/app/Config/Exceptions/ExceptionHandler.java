package com.reportService.app.Config.Exceptions;

import com.reportService.app.Models.Records.ServerMessage;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ServerMessage> handleTokenExpiredException(TokenExpiredException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerMessage("Session timeout."));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ServerMessage> handleBadRequestException(BadRequestException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ServerMessage> handleReportNotFoundException(ReportNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerMessage(e.getMessage()));
    }





}
