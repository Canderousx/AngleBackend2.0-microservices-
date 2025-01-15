package com.authService.app.Repositories;


import com.authService.app.Models.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {


    Optional<Account>findByUsername(String username);

    Optional<Account>findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<Account> findByActive(boolean active);

    @Query(value = "SELECT username FROM account WHERE id = :accountId",nativeQuery = true)
    String getUsernameById(@Param("accountId")String accountId);

    @Modifying
    @Query(value = "INSERT INTO account_subscribers(account_id, subscribers) VALUES (:channelId, :subscriber)",nativeQuery = true)
    @Transactional
    void addSubscriber(@Param("channelId") String channelId, @Param("subscriber") String subscriber);


    @Modifying
    @Query(value = "DELETE FROM account_subscribers WHERE account_id = :channelId AND subscribers = :subscriber", nativeQuery = true)
    @Transactional
    void removeSubscriber(@Param("channelId") String channelId, @Param("subscriber") String subscriber);


    @Query(value = "SELECT COUNT(*) > 0 FROM account_subscribers WHERE account_id = :channelId AND subscribers = :subscriber",nativeQuery = true)
    int isSubscriber(@Param("channelId") String channelId, @Param("subscriber") String subscriber);
    @Query(value = "SELECT COUNT(*) FROM account_subscribers WHERE account_id = :channelId", nativeQuery = true)
    long countSubscribers(@Param("channelId") String channelId);


    @Query(value = "SELECT a.id FROM account a JOIN account_subscribers s ON a.id = s.account_id WHERE s.subscribers = :accountId", nativeQuery = true)
    Page<String> findSubscribedChannels(@Param("accountId") String accountId, Pageable pageable);

    @Query(value = "SELECT s.subscribers FROM account_subscribers s WHERE s.account_id = :accountId", nativeQuery = true)
    Page<String> findSubscribers(@Param("accountId") String accountId, Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET active = false WHERE id = :accountId",nativeQuery = true)
    void banAccount(@Param("accountId")String accountId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET active = true WHERE id = :accountId",nativeQuery = true)
    void unbanAccount(@Param("accountId")String accountId);

}
