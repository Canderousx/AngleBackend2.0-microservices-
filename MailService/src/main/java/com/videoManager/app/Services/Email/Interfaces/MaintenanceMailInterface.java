package com.videoManager.app.Services.Email.Interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MaintenanceMailInterface {

    void restorePassword(String jsonData) throws JsonProcessingException;

    void passwordChangeMail(String email);

    void confirmationEmail(String jsonData) throws JsonProcessingException;

}
