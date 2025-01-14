package com.authService.app.Services.Subscription;


import com.authService.app.Models.Subscription;
import com.authService.app.Repositories.SubscriptionRepository;
import com.authService.app.Services.Cache.CacheService;
import com.authService.app.Services.Subscription.Interfaces.SubscriptionRetrievalInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionRetrievalService implements SubscriptionRetrievalInterface {

    private final SubscriptionRepository subscriptionRepository;

    private final CacheService cacheService;


    @Override
    public Subscription getSubscription(String accountId, String channelId) {
        return subscriptionRepository.findByAccountIdAndChannelId(accountId, channelId).orElse(null);
    }

    @Override
    @Cacheable(value = "auth_cache",key = "#accountId + '__random_channels_subs'")
    public List<String> getSubscribedChannelsOrderByRandom(String accountId, int quantity) {
        List<String> subs = subscriptionRepository.getSubscribedChannelsOrderByRandom(accountId,quantity);
        log.info("SUBS COUNT: "+subs.size());
        return subs;
    }

    @Override
    public Page<String> getSubscribedChannels(String accountId,int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return subscriptionRepository.getSubscribedChannels(accountId,pageable);
    }

    @Override
    public Page<String> getSubscribers(String channelId,int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return subscriptionRepository.getSubscribers(channelId,pageable);
    }

    @Override
    public boolean isSubscriber(String accountId, String channelId) {
        return cacheService.getWithCache(accountId+"__"+channelId+"__is_subscriber",subscriptionRepository::existsByAccountIdAndChannelId);
    }
}
