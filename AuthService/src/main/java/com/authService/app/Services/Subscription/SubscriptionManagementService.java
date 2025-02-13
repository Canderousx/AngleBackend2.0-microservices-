package com.authService.app.Services.Subscription;


import com.authService.app.Models.Subscription;
import com.authService.app.Repositories.SubscriptionRepository;
import com.authService.app.Services.Account.AccountRetrievalService;
import com.authService.app.Services.Notifications.NotificationGeneratorService;
import com.authService.app.Services.Subscription.Interfaces.SubscriptionManagementInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionManagementService implements SubscriptionManagementInterface {

    private final SubscriptionRetrievalService subscriptionRetrievalService;

    private final SubscriptionRepository subscriptionRepository;

    private final NotificationGeneratorService notificationGeneratorService;

    private final AccountRetrievalService accountRetrievalService;


    @Override
    @Transactional
    public void deleteSubscription(String accountId, String channelId) {
        subscriptionRepository.deleteByAccountIdAndChannelId(accountId,channelId);
    }

    @Override
    public void addSubscription(String accountId, String channelId) {
        if (!subscriptionRetrievalService.isSubscriber(accountId,channelId)){
            String username = accountRetrievalService.getUsername(accountId);
            Subscription sub = new Subscription();
            sub.setAccountId(accountId);
            sub.setChannelId(channelId);
            subscriptionRepository.save(sub);
            notificationGeneratorService.newSubscriberNotification(channelId,username,accountId);
        }
    }
}
