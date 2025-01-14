package com.authService.app.Services.Account;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Services.JwtService;
import com.authService.app.Config.Services.MyUserDetailsService;
import com.authService.app.Models.Account;
import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.Records.AuthRecord;
import com.authService.app.Models.Records.LoginRecord;
import com.authService.app.Services.Account.Interfaces.SignInInterface;
import com.authService.app.Services.Email.MaintenanceMailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class SignInService implements SignInInterface {

    private final AccountRetrievalService accountRetrievalService;

    private final MaintenanceMailService maintenanceMailService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final String loginFailMessage = "Invalid username or password.";

    @Autowired
    public SignInService(AccountRetrievalService accountRetrievalService, MaintenanceMailService maintenanceMailService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.accountRetrievalService = accountRetrievalService;
        this.maintenanceMailService = maintenanceMailService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthRecord signIn(LoginRecord loginRecord, String ipAddress) throws AccountNotFoundException, BadRequestException, JsonProcessingException {
        Account toLogin = accountRetrievalService.getRawAccountByEmail((loginRecord.email()));
        if(toLogin == null){
            System.out.println("null");
            throw new BadCredentialsException(loginFailMessage);
        }
        if(!accountRetrievalService.hasEmailConfirmed(toLogin.getEmail())){
            maintenanceMailService.confirmationEmail(toLogin.getEmail());
            throw new BadRequestException("You need to confirm your email address. Check your mailbox!");
        }
        try{
            if(accountRetrievalService.isBanned(toLogin.getId())){
                throw new BadRequestException("Account banned.");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(toLogin.getId(),loginRecord.password())
            );
            if(authentication.isAuthenticated()){
                return new AuthRecord(jwtService.generateToken(toLogin.getId(),ipAddress,toLogin.getAuthorities()));
            }else{
                throw new BadCredentialsException(loginFailMessage);
            }

        }catch (AuthenticationException e){
            throw new BadCredentialsException(loginFailMessage);
        }
    }

    @Override
    public void logout(String token) {
        jwtService.invalidateToken(token);
    }
}
