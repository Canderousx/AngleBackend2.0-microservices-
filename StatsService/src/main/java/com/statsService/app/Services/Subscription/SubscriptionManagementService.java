package com.statsService.app.Services.Subscription;


import com.statsService.app.Models.Subscription;
import com.statsService.app.Repositories.SubscriptionRepository;
import com.statsService.app.Services.JsonUtils;
import com.statsService.app.Services.Kafka.KafkaSenderService;
import com.statsService.app.Services.Subscription.Interfaces.SubscriptionManagementInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionManagementService implements SubscriptionManagementInterface {

    private final SubscriptionRetrievalService subscriptionRetrievalService;

    private final SubscriptionRepository subscriptionRepository;

    private final KafkaSenderService kafkaSenderService;

    @Override
    @Transactional
    public void deleteSubscription(String accountId, String channelId) {
        subscriptionRepository.deleteByAccountIdAndChannelId(accountId,channelId);
    }

    @Override
    public void addSubscription(String accountId, String channelId) {
        if (!subscriptionRetrievalService.isSubscriber(accountId,channelId)){
            Subscription sub = new Subscription();
            sub.setAccountId(accountId);
            sub.setChannelId(channelId);
            subscriptionRepository.save(sub);
            kafkaSenderService.send("new_subscription", JsonUtils.toJson(sub));
        }
    }
}
