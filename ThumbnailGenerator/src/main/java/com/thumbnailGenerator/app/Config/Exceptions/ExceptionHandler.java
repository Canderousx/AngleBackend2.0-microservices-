package com.thumbnailGenerator.app.Config.Exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    private final Logger logger = LogManager.getLogger(ExceptionHandler.class);

//    @org.springframework.web.bind.annotation.ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<ServerMessage> handleTokenExpiredException(ExpiredJwtException e){
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerMessage("Session timeout."));
//    }
//    @org.springframework.web.bind.annotation.ExceptionHandler(TokenExpiredException.class)
//    public ResponseEntity<ServerMessage> handleTokenExpiredException(TokenExpiredException e){
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServerMessage("Session timeout."));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(AccountNotFoundException.class)
//    public ResponseEntity<ServerMessage> handleAccountNotFoundException(AccountNotFoundException e){
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerMessage(e.getMessage()));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ServerMessage> handleBadRequestException(BadRequestException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getMessage()));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ServerMessage> handleBadCredentialsException(BadCredentialsException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getMessage()));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(CredentialsExistException.class)
//    public ResponseEntity<ServerMessage> handleCredentialsExistException(CredentialsExistException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getMessage()));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidDataException.class)
//    public ResponseEntity<ServerMessage> handleInvalidDataException(InvalidDataException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getMessage()));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ServerMessage> handleRuntimeException(RuntimeException e){
//        logger.error(e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServerMessage("Internal Server Error"));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(AuthorizationDeniedException.class)
//    public ResponseEntity<ServerMessage> handleRuntimeException(AuthorizationDeniedException e){
//        logger.error(e);
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ServerMessage("Access denied"));
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
//    public ResponseEntity<ServerMessage> validationExceptionHandler(MethodArgumentNotValidException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage(e.getBindingResult().getFieldError().getDefaultMessage()));
//    }




}
