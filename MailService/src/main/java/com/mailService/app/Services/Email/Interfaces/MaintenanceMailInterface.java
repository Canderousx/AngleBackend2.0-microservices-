package com.mailService.app.Services.Email.Interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MaintenanceMailInterface {

    void accountBannedMail(String jsonData) throws JsonProcessingException;

    void accountUnbannedMail(String jsonData) throws JsonProcessingException;
    void restorePassword(String jsonData) throws JsonProcessingException;

    void passwordChangeMail(String email);

    void confirmationEmail(String jsonData) throws JsonProcessingException;

}
