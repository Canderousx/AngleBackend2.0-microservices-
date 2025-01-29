package com.statsService.app.Services.Subscription.Interfaces;

public interface SubscriptionManagementInterface {

    void deleteSubscription(String accountId,String channelId);

    void addSubscription(String accountId,String channelId);
}
