package com.authService.app.Repositories;

import com.authService.app.Models.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    long countByChannelId(String channelId);

    boolean existsByAccountIdAndChannelId(String accountId, String channelId);

    void deleteByAccountIdAndChannelId(String accountId,String channelId);

    Optional<Subscription>findByAccountIdAndChannelId(String accountId,String channelId);

    @Query(value = "SELECT channel_id FROM subscription WHERE account_id = :accountId",nativeQuery = true)
    Page<String>getSubscribedChannels(@Param("accountId")String accountId, Pageable pageable);

    @Query(value = "SELECT account_id FROM subscription WHERE channel_id = :channelId",nativeQuery = true)
    Page<String>getSubscribers(@Param("channelId")String channelId,Pageable pageable);




}
