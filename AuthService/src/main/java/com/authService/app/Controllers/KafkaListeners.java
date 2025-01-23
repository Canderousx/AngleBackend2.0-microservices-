package com.authService.app.Controllers;

import com.authService.app.Models.Records.BanData;
import com.authService.app.Services.Account.AccountManagementService;
import com.authService.app.Services.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final AccountManagementService accountManagementService;

    @KafkaListener(topics = "account_banned",groupId = "auth_group")
    public void accountBannedEvent(String json) throws JsonProcessingException {
        BanData banData = JsonUtils.readJson(json, BanData.class);
        accountManagementService.banAccount(banData);
    }

    @KafkaListener(topics = "account_unbanned",groupId = "auth_group")
    public void accountUnbannedEvent(String json) throws JsonProcessingException {
        BanData banData = JsonUtils.readJson(json, BanData.class);
        accountManagementService.unbanAccount(banData);
    }




}
