package com.authService.app.Config.Services;

import com.authService.app.Models.Account;
import com.authService.app.Repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public Account loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Account> optAccount = accountRepository.findById(id);
        if(optAccount.isPresent()){
            return optAccount.get();
        }else{
            throw new UsernameNotFoundException("Account does not exist!");
        }
    }
}
