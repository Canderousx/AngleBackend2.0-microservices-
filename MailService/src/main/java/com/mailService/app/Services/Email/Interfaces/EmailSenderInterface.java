package com.mailService.app.Services.Email.Interfaces;

public interface EmailSenderInterface {

    void sendEmail(String to, String subject, String text);
}
