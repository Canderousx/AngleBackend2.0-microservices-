package com.authService.app.Services.Account.Interfaces;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Models.Records.VideoRating;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AccountManagement {

    void changeAvatar(MultipartFile avatar) throws IOException;

    void confirmEmail(String token) throws AccountNotFoundException, BadRequestException, JsonProcessingException;

    void restorePassword(String newPassword, String token) throws AccountNotFoundException, TokenExpiredException;

    void banAccount(String id) throws AccountNotFoundException;

    void unbanAccount(String id) throws AccountNotFoundException;


}
