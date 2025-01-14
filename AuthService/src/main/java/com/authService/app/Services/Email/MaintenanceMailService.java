package com.authService.app.Services.Email;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Services.JwtService;
import com.authService.app.Models.Account;
import com.authService.app.Models.EnvironmentVariables;
import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.Records.MaintenanceMail;
import com.authService.app.Services.Account.AccountRetrievalService;
import com.authService.app.Services.Account.SignUpService;
import com.authService.app.Services.Email.Interfaces.MaintenanceMailInterface;
import com.authService.app.Services.Kafka.KafkaSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceMailService implements MaintenanceMailInterface {

    private final JwtService jwtService;

    private final KafkaSenderService kafkaSenderService;

    private final AccountRetrievalService accountRetrievalService;

    @Autowired
    public MaintenanceMailService(JwtService jwtService, KafkaSenderService kafkaSenderService,
                                  AccountRetrievalService accountRetrievalService) {
        this.jwtService = jwtService;
        this.kafkaSenderService = kafkaSenderService;
        this.accountRetrievalService = accountRetrievalService;
    }

    private String mailDataToJson(String email, String token) throws JsonProcessingException {
        MaintenanceMail maintenanceMail = new MaintenanceMail(email,token);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(maintenanceMail);
    }

    @Override
    public void restorePassword(String email) throws JsonProcessingException {
        if(!accountRetrievalService.emailExists(email)){
            return;
        }
        Account toRestore = accountRetrievalService.getRawAccountByEmail(email);
        String token = jwtService.generatePasswordRecoveryToken(toRestore.getUsername());
        String jsonData = mailDataToJson(toRestore.getEmail(),token);
        kafkaSenderService.send(
                "restore_password_mail",
                jsonData
        );
    }

    @Override
    public void passwordChangeMail(String username){
        AccountRecord account = accountRetrievalService.getUserByUsername(username);
        kafkaSenderService.send(
                "password_changed_mail",
                account.email()
        );
    }

    @Override
    public void confirmationEmail(String email) throws JsonProcessingException {
        if(!accountRetrievalService.emailExists(email)){
            return;
        }
        AccountRecord account = accountRetrievalService.getUserByEmail(email);
        String token = jwtService.generateEmailConfirmationToken(account.username());
        String json = mailDataToJson(account.email(), token);
        kafkaSenderService.send(
                "email_confirmation_mail",
                json
        );
    }
}
