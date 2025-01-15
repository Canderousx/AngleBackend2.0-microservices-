package com.authService.app.Config.Filters;


import com.authService.app.Config.Exceptions.AccountBannedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.authService.app.Config.Services.JwtService;
import com.authService.app.Config.Services.MyUserDetailsService;
import com.authService.app.Models.Account;
import com.authService.app.Models.Records.ServerMessage;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {


    @Autowired
    JwtService jwtService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String authHeader = request.getHeader("Authentication");
        String userId = null;
        String token = null;
        try{
            if(authHeader != null){
                if(authHeader.startsWith("Bearer ")){
                    token = authHeader.substring(7);
                    userId = jwtService.extractUserId(token);

                }
            }
            if(userId !=null && SecurityContextHolder.getContext().getAuthentication() == null){
                Account account = userDetailsService.loadUserByUsername(userId);
                if(!account.isActive()){
                    throw new AccountBannedException("Account banned.");
                }
                String userIP = request.getRemoteAddr();
                if(jwtService.validateToken(token,account,userIP)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userId,null,account.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServerMessage serverMessage = new ServerMessage("Session timed out!");
            String jsonResponse = new ObjectMapper().writeValueAsString(serverMessage);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        }catch (AuthorizationDeniedException | AuthenticationException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } catch (AccountBannedException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServerMessage serverMessage = new ServerMessage(e.getMessage());
            String jsonResponse = new ObjectMapper().writeValueAsString(serverMessage);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        }
    }
}