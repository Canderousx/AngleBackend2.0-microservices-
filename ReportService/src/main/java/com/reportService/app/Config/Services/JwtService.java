package com.reportService.app.Config.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private final Set<String> invalidatedTokens = new HashSet<>();

    @Autowired
    private EnvironmentVariables environmentVariables;


    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(environmentVariables.getJTokenKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String extractIP(String token){
        return extractClaim(token, claims -> {
            return (String) claims.get("IP");
        });
    }
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }
    public Boolean validateToken(String token, String email, String userIP){
        if (token == null || token.isEmpty()) {
            return false;
        }
        Map<String, Object> claims = extractAllClaims(token);
        if (claims.containsKey("src")) {
            return false;
        }
        final String username = extractUsername(token);
        final String savedIP = extractIP(token);
        return username.equals(email) && !isTokenExpired(token) && (savedIP.equals(userIP));
    }
}