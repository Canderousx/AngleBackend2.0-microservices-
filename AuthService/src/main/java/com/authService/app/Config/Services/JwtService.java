package com.authService.app.Config.Services;

import com.authService.app.Models.Account;
import com.authService.app.Models.EnvironmentVariables;
import com.authService.app.Models.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final Set<String> invalidatedTokens = new HashSet<>();

    @Autowired
    private EnvironmentVariables environmentVariables;


    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(environmentVariables.getJTokenKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(environmentVariables.getJTokenKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private String createToken(Map<String,Object> claims, String userId, long time){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .signWith(getSignKey())
                .compact();
    }

    public String generateToken(String userId, Collection<? extends GrantedAuthority> roles){
        Map<String,Object> claims = new HashMap<>();
        claims.put(
                "roles",
                roles.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        return createToken(claims,userId,15 * 60 * 1000);
    }


    public String generatePasswordRecoveryToken(String id){
        Map<String,Object>claims = new HashMap<>();
        claims.put("src","passwordRecovery");
        return createToken(claims,id,15 * 60 * 1000);
    }

    public String generateEmailConfirmationToken(String id){
        Map<String,Object>claims = new HashMap<>();
        claims.put("src","emailConfirmation");
        return createToken(claims,id,15 * 60 * 1000);
    }



    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Collection<? extends GrantedAuthority>extractAuthorities(String token){
        return extractClaim(token, claims -> {
            return (Collection<? extends GrantedAuthority>) claims.get("roles");
        });
    }


    public String extractUserId(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }
    public Boolean validateToken(String token, Account userDetails, String userIP){
        if (token == null || token.isEmpty()) {
            return false;
        }
        Map<String, Object> claims = extractAllClaims(token);
        if (claims.containsKey("src")) {
            return false;
        }
        final String userId = extractUserId(token);
        return userId.equals(userDetails.getId()) && !isTokenExpired(token) && !this.invalidatedTokens.contains(token);
    }

    public Boolean validatePasswordRecoveryToken(String token){
        if (token == null || token.isEmpty()) {
            return false;
        }
        Map<String, Object> claims = extractAllClaims(token);
        if (claims == null || !claims.containsKey("src")) {
            return false;
        }
        String src = claims.get("src").toString();
        return !this.invalidatedTokens.contains(token) && !isTokenExpired(token)
                && "passwordRecovery".equals(src);
    }

    public Boolean validateEmailConfirmationToken(String token){
        if (token == null || token.isEmpty()) {
            return false;
        }
        Map<String, Object> claims = extractAllClaims(token);
        if (claims == null || !claims.containsKey("src")) {
            return false;
        }
        String src = claims.get("src").toString();
        return !this.invalidatedTokens.contains(token) && !isTokenExpired(token) && "emailConfirmation".equals(src);
    }





    public void invalidateToken(String token){
        this.invalidatedTokens.add(token);
    }








}