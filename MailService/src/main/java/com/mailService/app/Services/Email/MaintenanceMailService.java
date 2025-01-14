package com.mailService.app.Services.Email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailService.app.Models.EnvironmentVariables;
import com.mailService.app.Models.MaintenanceMailData;
import com.mailService.app.Services.Email.Interfaces.MaintenanceMailInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceMailService implements MaintenanceMailInterface {


    private final EmailSenderService emailSenderService;

    private final EnvironmentVariables environmentVariables;

    @Autowired
    public MaintenanceMailService(EmailSenderService emailSenderService,EnvironmentVariables environmentVariables) {
        this.emailSenderService = emailSenderService;
        this.environmentVariables = environmentVariables;
    }

    private MaintenanceMailData readJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json,MaintenanceMailData.class);
    }

    @Override
    public void restorePassword(String jsonData) throws JsonProcessingException {
        MaintenanceMailData mailData = readJson(jsonData);
        String restoreUrl = "/restorePassword?id="+mailData.token();
        String message = "Dear Sir or Madam"+", \n\n\n\n" +
                "Here is the link to restore your password :\n" +
                environmentVariables.getAngleFrontUrl()+restoreUrl+"\n" +
                "The link will expire in 15 minutes since your submission. Do not share it!";
        this.emailSenderService.sendEmail(mailData.email(),"Password Restoration",message);


    }

    @Override
    public void passwordChangeMail(String email){
        String message = "Dear Sir or Madam"+", \n\n\n\n" +
                "Your password has been changed. If it wasn't you, please contact us immediately!\n";
        emailSenderService.sendEmail(email,"Your password has been changed",message);

    }

    @Override
    public void confirmationEmail(String jsonData) throws JsonProcessingException {
        MaintenanceMailData mailData = readJson(jsonData);
        String message = "Dear Sir or Madam"+",\n\n\n\n" +
                "Here is your confirmation link to activate your angle account: \n" +
                environmentVariables.getAngleFrontUrl()+"/confirmAccount?id="+mailData.token();
        emailSenderService.sendEmail(mailData.email(),"Account Email Confirmation",message);
    }
}
