package com.authService.app.Controllers;


import com.authService.app.Config.Exceptions.AccountNotFoundException;
import com.authService.app.Models.Account;
import com.authService.app.Models.Records.AccountRecord;
import com.authService.app.Models.Records.ServerMessage;
import com.authService.app.Services.Account.AccountManagementService;
import com.authService.app.Services.Account.AccountRetrievalService;
import com.authService.app.Services.Account.AvatarService;
import com.authService.app.Services.Subscription.SubscriptionRetrievalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts/")
public class AccountData {

    private final AccountRetrievalService accountRetrievalService;

    private final AccountManagementService accountManagementService;

    private final SubscriptionRetrievalService subscriptionRetrievalService;

    @RequestMapping(value = "getCurrentUser",method = RequestMethod.GET)
    public AccountRecord getCurrentUser() throws BadRequestException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        if(id == null){
            return null;
        }
        return accountRetrievalService.getUserById(id);
    }

    @RequestMapping(value = "emailExists",method = RequestMethod.GET)
    public boolean emailExists(@RequestParam String email){
        return accountRetrievalService.emailExists(email);
    }

    @RequestMapping(value = "usernameExists",method = RequestMethod.GET)
    public boolean usernameExists(@RequestParam String username){
        return accountRetrievalService.usernameExists(username);
    }
    @RequestMapping(value = "getUsername",method = RequestMethod.GET)
    public String getUsername(@RequestParam String id){
        return accountRetrievalService.getUsername(id);
    }

    @RequestMapping(value = "getUserById",method = RequestMethod.GET)
    public AccountRecord getUserById(@RequestParam String id){
        return accountRetrievalService.getUserById(id);
    }

    @RequestMapping(value = "setAvatar",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>setAvatar(@RequestBody MultipartFile avatar) throws IOException {
        accountManagementService.changeAvatar(avatar);
        return ResponseEntity.ok().body(new ServerMessage("Avatar has been saved"));
    }

    @RequestMapping(value = "media/getAvatar",method = RequestMethod.GET)
    public ResponseEntity<Resource>getAvatar(@RequestParam String userId, HttpServletRequest request) throws IOException {
        Resource avatarFile = accountRetrievalService.getAvatar(userId);
        String eTag = "\""+String.valueOf(avatarFile.lastModified())+"\"";
        String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
        }
        return ResponseEntity.ok()
                .eTag(eTag)
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(avatarFile.getFile().toPath()))
                .cacheControl(CacheControl
                        .noCache()
                        .cachePublic()
                        .mustRevalidate()
                )
                .body(avatarFile);
    }

    @RequestMapping(value = "getSubscribedChannelsRandom",method = RequestMethod.GET)
    public List<String> getSubscribedIds(@RequestParam int quantity){
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        return subscriptionRetrievalService.getSubscribedChannelsOrderByRandom(accountId,quantity);
    }

    @RequestMapping(value = "amAdmin",method = RequestMethod.GET)
    public boolean amIAdmin() throws AccountNotFoundException {
        return accountRetrievalService.isAdmin(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }

    @RequestMapping(value = "countSubscribers",method = RequestMethod.GET)
    public long countSubscribers(@RequestParam String id){
        return accountRetrievalService.countSubscribers(id);
    }
}
