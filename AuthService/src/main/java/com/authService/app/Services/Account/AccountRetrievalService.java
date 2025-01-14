package com.authService.app.Services.Account;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Models.Account;
import com.authService.app.Models.EnvironmentVariables;
import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.UserRole;
import com.authService.app.Repositories.AccountRepository;
import com.authService.app.Services.Account.Interfaces.AccountRetrieval;
import com.authService.app.Services.Cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountRetrievalService implements AccountRetrieval {

    private final AccountRepository accountRepository;

    private final CacheService cacheService;

    private final EnvironmentVariables environmentVariables;


    @Override
    @Cacheable(value = "auth_cache",key = "#id +'__username'")
    public String getUsername(String id) {
        return accountRepository.getUsernameById(id);
    }

    @Override
    public boolean hasEmailConfirmed(String email) throws AccountNotFoundException {
        Account account = getRawAccountByEmail(email);
        return account.isConfirmed();
    }

    @Override
    public boolean isBanned(String id) throws AccountNotFoundException {
        Account account = getRawAccountById(id);
        return !account.isActive();
    }

    @Override
    public boolean isAdmin(String id) throws AccountNotFoundException {
        Account account = getRawAccountById(id);
        for(UserRole roles : account.getRoles()){
            if(roles.getName().equals("ROLE_ADMIN")){
                return true;
            }
        }
        return false;
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
    public boolean isModerator(String id) throws AccountNotFoundException {
        Account account = getRawAccountById(id);
        for(UserRole roles : account.getRoles()){
            if(roles.getName().equals("ROLE_MODERATOR") || roles.getName().equals("ROLE_ADMIN")){
                return true;
            }
        }
        return false;
    }

    private AccountRecord toRecord(Account account){
        return new AccountRecord(
                account.getId(),
                account.getUsername(),
                account.getAvatar(),
                account.getEmail()
        );
    }

    @Override
    public AccountRecord getCurrentUser() throws BadRequestException {
        Account account = getRawCurrentUser();
        return toRecord(account);
    }

    @Override
    public Account getRawCurrentUser() throws BadRequestException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userId == null){
            throw new BadRequestException("You need to log in first!");
        }

        return cacheService.getWithCache(userId,this::getRawAccountById);
    }

    @Override
    public AccountRecord getUserById(String id) {
        Account account = cacheService.getWithCache(id,this::getRawAccountById);
        return toRecord(account);
    }

    @Override
    public AccountRecord getUserByUsername(String username){
        Account account = cacheService.getWithCache(username,this::getRawAccountByUsername);
        return toRecord(account);
    }

    @Override
    public AccountRecord getUserByEmail(String email) {
        Account account = cacheService.getWithCache(email,this::getRawAccountByEmail);
        return toRecord(account);
    }

    @Override
    public Account getRawAccountById(String id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if(accountOpt.isEmpty()){
            log.info("Account "+id+" not found.");
            return null;
        }
        return accountOpt.get();
    }

    @Override
    public Account getRawAccountByUsername(String username) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if(accountOpt.isEmpty()){
            log.info("Account not found.");
            return null;
        }
        return accountOpt.get();
    }

    @Override
    public Account getRawAccountByEmail(String email) {
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if(accountOpt.isEmpty()){
            log.info("Account not found.");
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
