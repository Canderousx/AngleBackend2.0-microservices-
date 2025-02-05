package com.authService.app.Services.Account;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Config.Exceptions.CredentialsExistException;
import com.authService.app.Models.Account;
import com.authService.app.Models.Records.NewUserRecord;
import com.authService.app.Repositories.AccountRepository;
import com.authService.app.Services.Account.Interfaces.SignUpInterface;
import com.authService.app.Services.Email.MaintenanceMailService;
import com.authService.app.Services.UserRole.UserRoleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService implements SignUpInterface {

    private final AccountRepository accountRepository;

    private final AccountRetrievalService accountRetrievalService;

    private final PasswordEncoder passwordEncoder;

    private final MaintenanceMailService maintenanceMailService;

    private final UserRoleService userRoleService;


    @Autowired
    public SignUpService(AccountRepository accountRepository, AccountRetrievalService accountRetrievalService, PasswordEncoder passwordEncoder, MaintenanceMailService maintenanceMailService,
                         UserRoleService userRoleService) {
        this.accountRepository = accountRepository;
        this.accountRetrievalService = accountRetrievalService;
        this.passwordEncoder = passwordEncoder;
        this.maintenanceMailService = maintenanceMailService;
        this.userRoleService = userRoleService;
    }



    @Override
    public void signUp(NewUserRecord newUser) throws CredentialsExistException, AccountNotFoundException, JsonProcessingException {
        if(accountRetrievalService.emailExists(newUser.email()) || accountRetrievalService.usernameExists(newUser.username())){
            throw new CredentialsExistException("Account with given credentials already exist.");
        }
        Account account = new Account();
        account.setUsername(newUser.username());
        account.setEmail(newUser.email());
        account.setPassword(passwordEncoder.encode(newUser.password()));
        account.setConfirmed(false);
        account.setActive(true);
        account.getRoles().add(userRoleService.getByRoleName("ROLE_USER"));
        accountRepository.save(account);
        maintenanceMailService.confirmationEmail(account.getId());
    }
}
