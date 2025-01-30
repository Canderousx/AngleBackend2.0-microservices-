package com.Notifications.app.Config.Exceptions;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        String errorMsg;
        if(ex instanceof TokenExpiredException){
            errorMsg = "TOKEN EXPIRED";
        }else{
            errorMsg = "INTERNAL SERVER ERROR";
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorMsg);
        byte[] payload = new byte[0];
        return MessageBuilder.createMessage(payload,accessor.getMessageHeaders());
    }
}
