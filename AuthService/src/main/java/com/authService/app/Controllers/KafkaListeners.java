package com.authService.app.Controllers;


import com.authService.app.Models.Records.VideoRating;
import com.authService.app.Services.Account.AccountManagementService;
import com.authService.app.Services.JsonUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final AccountManagementService accountManagementService;

    public KafkaListeners(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

}
