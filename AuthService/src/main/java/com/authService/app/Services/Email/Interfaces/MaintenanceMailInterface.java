package com.authService.app.Services.Email.Interfaces;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface MaintenanceMailInterface {

    void restorePassword(String email) throws AccountNotFoundException, JsonProcessingException;

    void passwordChangeMail(String username) throws AccountNotFoundException;

    void confirmationEmail(String email) throws AccountNotFoundException, JsonProcessingException;

}
