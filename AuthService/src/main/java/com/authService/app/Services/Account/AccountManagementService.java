package com.authService.app.Services.Account;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Config.Services.JwtService;
import com.authService.app.Models.Account;
import com.authService.app.Repositories.AccountRepository;
import com.authService.app.Services.Account.Interfaces.AccountManagement;
import com.authService.app.Services.Email.MaintenanceMailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AccountManagementService implements AccountManagement {

    private final AccountRetrievalService accountRetrievalService;

    private final MaintenanceMailService maintenanceMailService;
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final AvatarService avatarService;

    private final JwtService jwtService;


    @Override
    public void confirmEmail(String token) throws AccountNotFoundException, BadRequestException, JsonProcessingException {
        Account account = accountRetrievalService.getRawAccountByUsername(jwtService.extractUserId(token));
        if(jwtService.validateEmailConfirmationToken(token)){
            account.setConfirmed(true);
            accountRepository.save(account);
            jwtService.invalidateToken(token);
            return;
        }
        maintenanceMailService.confirmationEmail(account.getEmail());
        throw new BadRequestException("Confirmation timeout! New confirmation email has been sent!");


    }

    @Override
    public void restorePassword(String newPassword, String token) throws AccountNotFoundException, TokenExpiredException {
        if (jwtService.validatePasswordRecoveryToken(token)){
            Account account = accountRetrievalService.getRawAccountByUsername(jwtService.extractUserId(token));
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);
            jwtService.invalidateToken(token);
            return;
        }
        throw new TokenExpiredException("Access denied. Please try again.");
    }

    @Override
    public void banAccount(String id){
        accountRepository.banAccount(id);
    }

    @Override
    public void unbanAccount(String id){
        accountRepository.unbanAccount(id);
    }

    @Override
    public void changeAvatar(MultipartFile avatar) throws IOException {
        if(!avatarService.checkExtension(avatar)){
            throw new InvalidFileNameException("","File extension not supported!");
        }
        Account account = accountRetrievalService.getRawCurrentUser();
        account.setAvatar(
                avatarService.saveAvatarFile(
                        account.getId(),
                        avatar
                )
        );
        accountRepository.save(account);
    }


}
