package com.authService.app.Services.Email.Interfaces;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Models.Records.BanData;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface MaintenanceMailInterface {

    void accountUnbanned(BanData banData) throws JsonProcessingException;
    void accountBanned(BanData banData) throws JsonProcessingException;
    void restorePassword(String email) throws AccountNotFoundException, JsonProcessingException;
    void confirmationEmail(String email) throws AccountNotFoundException, JsonProcessingException;

}
