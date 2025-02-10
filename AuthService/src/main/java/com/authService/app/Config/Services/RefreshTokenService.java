package com.authService.app.Config.Services;

import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Config.Exceptions.UnknownRefreshTokenException;
import com.authService.app.Models.RefreshToken;
import com.authService.app.Repositories.RefreshTokenRepository;
import com.authService.app.Services.Account.AccountRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    public RefreshToken createRefreshToken(String userId,String fingerprint){
        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(userId)
                .fingerprint(fingerprint)
                .expirationDate(Instant.now().plusSeconds(604800))//one week expiration - 604 800 seconds
                .token(UUID.randomUUID().toString())
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findToken(String token){
        return refreshTokenRepository.findByToken(token).orElse(null);
    }

    public void removeRefreshToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }
    public void removeUserTokens(String accountId){
        refreshTokenRepository.deleteAllByAccountId(accountId);
    }

    public boolean validateRefreshToken(String token, String fingerprint) throws UnknownRefreshTokenException, TokenExpiredException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new UnknownRefreshTokenException("Refresh token unrecognized!")
        );
        if (!refreshToken.getFingerprint().equals(fingerprint)){
            return false;
        }
        if(refreshToken.getExpirationDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Refresh token has expired.");
        }
        return true;
    }






}
