package com.videoManager.app.Controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.videoManager.app.Services.Email.MaintenanceMailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final MaintenanceMailService mailService;

    private final Logger logger = LogManager.getLogger(KafkaListeners.class);

    @Autowired
    public KafkaListeners(MaintenanceMailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = "restore_password_mail",groupId = "mail-group")
    void restorePasswordMail(String data) throws JsonProcessingException {
        logger.info("Received password restoration request.");
        mailService.restorePassword(data);
    }

    @KafkaListener(topics = "password_changed_mail",groupId = "mail-group")
    void passwordChangedMail(String email) throws JsonProcessingException {
        mailService.passwordChangeMail(email);
    }
    @KafkaListener(topics = "email_confirmation_mail",groupId = "mail-group")
    void confirmEmail(String data) throws JsonProcessingException {
        logger.info("Received email confirmation request.");
        mailService.confirmationEmail(data);
    }
}
