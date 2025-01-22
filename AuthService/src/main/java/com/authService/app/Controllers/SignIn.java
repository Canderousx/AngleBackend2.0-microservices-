package com.authService.app.Controllers;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Config.Exceptions.UnknownRefreshTokenException;
import com.authService.app.Models.Records.AuthRecord;
import com.authService.app.Models.Records.LoginRecord;
import com.authService.app.Models.Records.ServerMessage;
import com.authService.app.Services.Account.SignInService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/signIn")
@RequiredArgsConstructor
public class SignIn {

    private final SignInService signInService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    public AuthRecord signIn(@RequestBody LoginRecord loginRecord, HttpServletRequest request) throws BadRequestException, AccountNotFoundException, JsonProcessingException {
        return this.signInService.signIn(loginRecord,request.getRemoteAddr());
    }

    @RequestMapping(value = "/refresh",method = RequestMethod.POST)
    public String refreshToken(@RequestBody Map<String,Object> refreshRequest,HttpServletRequest request) throws UnknownRefreshTokenException, TokenExpiredException {
        String refreshToken = (String) refreshRequest.get("refreshToken");
        return signInService.refreshAccessToken(refreshToken,request.getRemoteAddr());
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>logout(HttpServletRequest request,@RequestBody String refreshToken){
        String token = request.getHeader("Authentication").substring(7);
        signInService.logout(token,refreshToken);
        return ResponseEntity.ok().body(new ServerMessage("You've been signed out."));
    }


}
