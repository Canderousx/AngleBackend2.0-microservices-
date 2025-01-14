package com.authService.app.Controllers;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.CredentialsExistException;
import com.authService.app.Models.Records.NewUserRecord;
import com.authService.app.Models.Records.ServerMessage;
import com.authService.app.Services.Account.AccountManagementService;
import com.authService.app.Services.Account.SignUpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/signUp")
@RequiredArgsConstructor
public class SignUp {

    private final SignUpService signUpService;

    private final AccountManagementService accountManagementService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage> signUp(@RequestBody @Validated NewUserRecord newUserRecord) throws CredentialsExistException, AccountNotFoundException, JsonProcessingException {
        signUpService.signUp(newUserRecord);
        return ResponseEntity.ok(new ServerMessage("Account has been created. In order to login you need to confirm your email. Check your mailbox"));
    }

    @RequestMapping(value = "/confirmEmail",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage> confirmEmail(@RequestParam String token) throws BadRequestException, AccountNotFoundException, JsonProcessingException {
        this.accountManagementService.confirmEmail(token);
        return ResponseEntity.ok(new ServerMessage("Email has been confirmed. You can now sign in."));
    }


}
