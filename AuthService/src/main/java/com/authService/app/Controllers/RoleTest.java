package com.authService.app.Controllers;

import com.authService.app.Models.Records.ServerMessage;
import com.authService.app.Services.Account.AccountRetrievalService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rolesTest")
public class RoleTest {

    @Autowired
    private AccountRetrievalService accountRetrievalService;

    @RequestMapping(value = "/adminTest",method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ServerMessage>amIAdmin() throws BadRequestException {
        accountRetrievalService.getCurrentUser();
        return ResponseEntity.ok().body(new ServerMessage("Hello there admin!"));
    }
    @RequestMapping(value = "/moderatorTest",method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<ServerMessage>amIModerator() throws BadRequestException {
        accountRetrievalService.getCurrentUser();
        return ResponseEntity.ok().body(new ServerMessage("Hello there moderator!"));
    }

}
