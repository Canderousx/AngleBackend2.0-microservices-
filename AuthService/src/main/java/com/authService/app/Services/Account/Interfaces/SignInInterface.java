package com.authService.app.Services.Account.Interfaces;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Config.Exceptions.UnknownRefreshTokenException;
import com.authService.app.Models.Records.AuthRecord;
import com.authService.app.Models.Records.LoginRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;

public interface SignInInterface {

    String refreshAccessToken(String refreshToken,String fingerprint) throws UnknownRefreshTokenException, TokenExpiredException;
    AuthRecord signIn(LoginRecord loginRecord) throws AccountNotFoundException, BadRequestException, JsonProcessingException;

    void logout(String token,String refreshToken);

}
