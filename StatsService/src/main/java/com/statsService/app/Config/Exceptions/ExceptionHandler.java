package com.statsService.app.Config.Exceptions;

import com.statsService.app.Models.Records.ServerMessage;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    private final Logger logger = LogManager.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ServerMessage> handleBadRequestException(BadRequestException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ServerMessage> handleRuntimeException(RuntimeException e){
        logger.error(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServerMessage("Internal Server Error"));
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ServerMessage> validationExceptionHandler(MethodArgumentNotValidException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getBindingResult().getFieldError().getDefaultMessage()));
    }





}
