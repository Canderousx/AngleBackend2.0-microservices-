package com.authService.app.Services.Account;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Models.Account;
import com.authService.app.Models.EnvironmentVariables;
import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.UserRole;
import com.authService.app.Repositories.AccountRepository;
import com.authService.app.Repositories.SubscriptionRepository;
import com.authService.app.Services.Account.Interfaces.AccountRetrieval;
import com.authService.app.Services.Cache.AuthCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountRetrievalService implements AccountRetrieval {

    private final AccountRepository accountRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final EnvironmentVariables environmentVariables;

    private final AuthCache authCache;


    @Override
    public String getEmail(String id) {
        return getUserById(id).email();
    }

    @Override
    public String getUsername(String id) {
        return getUserById(id).username();
    }

    @Override
    public boolean hasEmailConfirmed(String email) {
        Account account = getRawAccountByEmail(email);
        return account.isConfirmed();
    }

    @Override
    public boolean isBanned(String id) {
        Account account = getRawAccountById(id);
        return !account.isActive();
    }

    private boolean adminTest(Set<UserRole> accountRoles){
        for(UserRole roles : accountRoles){
            if(roles.getName().equals("ROLE_ADMIN")){
                return true;
            }
        }
        return false;
    }
    private boolean moderatorTest(Set<UserRole>accountRoles){
        for (UserRole roles : accountRoles){
            if(roles.getName().equals("ROLE_MODERATOR") || roles.getName().equals("ROLE_ADMIN")){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAdmin(String id){
        return getUserById(id).admin();
    }

    @Override
    public boolean emailExists(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public boolean usernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    public boolean isModerator(String id) {
        return getUserById(id).moderator();
    }


    private AccountRecord toRecord(Account account){
        boolean isAdmin = adminTest(account.getRoles());
        return new AccountRecord(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                isAdmin,
                isAdmin || moderatorTest(account.getRoles())
        );
    }

    @Override
    public AccountRecord getCurrentUser() throws BadRequestException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userId == null){
            throw new BadRequestException("You need to log in first!");
        }
        return getUserById(userId);
    }

    @Override
    public Account getRawCurrentUser() throws BadRequestException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userId == null){
            throw new BadRequestException("You need to log in first!");
        }
        return getRawAccountById(userId);
    }

    @Override
    public long countSubscribers(String channelId) {
        return subscriptionRepository.countByChannelId(channelId);
    }

    @Override
    public AccountRecord getUserById(String id) {
        String redisKey = authCache.getAccountKey(id);
        return authCache.getFromCacheOrFetch(redisKey, AccountRecord.class,()->{
            Account account = getRawAccountById(id);
            return toRecord(account);
        });
    }

    @Override
    public Account getRawAccountById(String id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if(accountOpt.isEmpty()){
            log.info("Account {} not found.",id);
            return null;
        }
        return accountOpt.get();
    }
    @Override
    public Account getRawAccountByEmail(String email) {
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if(accountOpt.isEmpty()){
            log.info("Account {} not found.",email);
            return null;
        }
        return accountOpt.get();
    }

    @Override
    public FileSystemResource getAvatar(String id) {
        Account account = getRawAccountById(id);
        return new FileSystemResource(
                environmentVariables.getAvatarsPath()+ File.separator+account.getAvatar()
        );
    }
}
