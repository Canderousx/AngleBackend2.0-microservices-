package com.authService.app.Services.Account;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Config.Exceptions.UnknownRefreshTokenException;
import com.authService.app.Config.Services.JwtService;
import com.authService.app.Config.Services.MyUserDetailsService;
import com.authService.app.Config.Services.RefreshTokenService;
import com.authService.app.Models.Account;
import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.Records.AuthRecord;
import com.authService.app.Models.Records.LoginRecord;
import com.authService.app.Models.RefreshToken;
import com.authService.app.Services.Account.Interfaces.SignInInterface;
import com.authService.app.Services.Email.MaintenanceMailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService implements SignInInterface {

    private final AccountRetrievalService accountRetrievalService;

    private final MaintenanceMailService maintenanceMailService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final String loginFailMessage = "Invalid username or password.";

    @Override
    public AuthRecord signIn(LoginRecord loginRecord) throws BadRequestException, JsonProcessingException {
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
                String refreshToken = refreshTokenService.createRefreshToken(toLogin.getId(),loginRecord.fingerprint()).getToken();
                return new AuthRecord(jwtService.generateToken(toLogin.getId(),toLogin.getAuthorities()),refreshToken);
            }else{
                throw new BadCredentialsException(loginFailMessage);
            }

        }catch (AuthenticationException e){
            throw new BadCredentialsException(loginFailMessage);
        }
    }

    @Override
    public String refreshAccessToken(String refreshToken,String fingerprint) throws UnknownRefreshTokenException, TokenExpiredException {
        if(refreshToken == null || fingerprint == null){
            throw new UnknownRefreshTokenException("Refresh token or fingerprint is null!");
        }
        if(!refreshTokenService.validateRefreshToken(refreshToken,fingerprint)){
            return null;
        }
        RefreshToken refresh = refreshTokenService.findToken(refreshToken);
        Account user = accountRetrievalService.getRawAccountById(refresh.getAccountId());
        return jwtService.generateToken(user.getId(),user.getAuthorities());
    }

    @Override
    public void logout(String token,String refreshToken) {
        refreshTokenService.removeRefreshToken(refreshToken);
        jwtService.invalidateToken(token);
    }
}
