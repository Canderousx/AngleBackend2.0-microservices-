package com.authService.app.Services.Account.Interfaces;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.CredentialsExistException;
import com.authService.app.Models.Records.NewUserRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface SignUpInterface {

    void signUp(NewUserRecord newUser) throws CredentialsExistException, AccountNotFoundException, JsonProcessingException;
}
