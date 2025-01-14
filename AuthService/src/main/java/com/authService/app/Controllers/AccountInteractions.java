package com.authService.app.Controllers;


import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.Records.ServerMessage;
import com.authService.app.Services.Account.AccountRetrievalService;
import com.authService.app.Services.Subscription.SubscriptionManagementService;
import com.authService.app.Services.Subscription.SubscriptionRetrievalService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/")
public class AccountInteractions {

    private final SubscriptionRetrievalService subscriptionRetrievalService;

    private final SubscriptionManagementService subscriptionManagementService;


    @RequestMapping(value = "subscribe",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>subscribeChannel(@RequestParam String channelId) throws BadRequestException {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        subscriptionManagementService.addSubscription(accountId,channelId);
        return ResponseEntity.ok().body(new ServerMessage("Channel has been subscribed"));
    }
    @RequestMapping(value = "unsubscribe",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>unsubscribeChannel(@RequestParam String channelId) throws BadRequestException {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        subscriptionManagementService.deleteSubscription(accountId,channelId);
        return ResponseEntity.ok().body(new ServerMessage("Channel has been unsubscribed"));
    }

    @RequestMapping(value = "isSubscriber",method = RequestMethod.GET)
    public boolean isSubscribing(@RequestParam String id){
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        return subscriptionRetrievalService.isSubscriber(accountId,id);
    }



}
