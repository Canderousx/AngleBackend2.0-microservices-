package com.authService.app.Services.Account.Interfaces;

import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Models.Account;
import com.authService.app.Models.Records.AccountRecord;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;

public interface AccountRetrieval {


    boolean hasEmailConfirmed(String email) throws AccountNotFoundException;

    boolean isBanned(String id) throws AccountNotFoundException;

    boolean isAdmin(String id) throws AccountNotFoundException;

    boolean isModerator(String id) throws AccountNotFoundException;

    boolean usernameExists(String username);

    boolean emailExists(String email);

    long countSubscribers(String channelId);

    String getUsername(String id);

    FileSystemResource getAvatar(String id);

    AccountRecord getCurrentUser() throws BadRequestException;

    Account getRawCurrentUser() throws BadRequestException;

    AccountRecord getUserById(String id) throws AccountNotFoundException;

    AccountRecord getUserByUsername(String username) throws AccountNotFoundException;

    AccountRecord getUserByEmail(String email) throws AccountNotFoundException;

    Account getRawAccountById(String id) throws AccountNotFoundException;

    Account getRawAccountByUsername(String username) throws AccountNotFoundException;

    Account getRawAccountByEmail(String email) throws AccountNotFoundException;

}
