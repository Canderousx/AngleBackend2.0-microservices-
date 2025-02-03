package com.authService.app.Services.Subscription.Interfaces;

import com.authService.app.Models.Subscription;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SubscriptionRetrievalInterface {

    Subscription getSubscription(String accountId, String channelId);

    List<String> getSubscribedChannels(String accountId, int quantity);

    Page<String> getSubscribedChannels(String accountId,int page, int pageSize);

    Page<String>getSubscribers(String channelId,int page, int pageSize);

    boolean isSubscriber(String accountId, String channelId);


}
