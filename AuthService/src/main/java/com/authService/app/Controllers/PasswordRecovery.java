package com.authService.app.Controllers;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.TokenExpiredException;
import com.authService.app.Models.Records.NewPasswordRecord;
import com.authService.app.Models.Records.ServerMessage;
import com.authService.app.Services.Account.AccountManagementService;
import com.authService.app.Services.Email.MaintenanceMailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/signUp")
@RequiredArgsConstructor
public class PasswordRecovery {

    private final MaintenanceMailService maintenanceMailService;

    private final AccountManagementService accountManagementService;

    @RequestMapping(value = "/recoverPassword",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>recoverPassword(@RequestBody Map<String,Object>data) throws JsonProcessingException {
        String email = (String) data.get("email");
        maintenanceMailService.restorePassword(email);
        return ResponseEntity.ok().body(new ServerMessage("An email with password restoration has been sent. Check your mailbox"));
    }

    @RequestMapping(value = "/passwordRecovery",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>passwordRecovery(@RequestBody NewPasswordRecord newPasswordRecord) throws TokenExpiredException, AccountNotFoundException {
        accountManagementService.restorePassword(newPasswordRecord.password(), newPasswordRecord.token());
        return ResponseEntity.ok().body(new ServerMessage("Your password has been changed. You can now sign in."));
    }
}
