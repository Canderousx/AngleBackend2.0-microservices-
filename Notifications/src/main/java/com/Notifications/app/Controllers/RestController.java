package com.Notifications.app.Controllers;


import com.Notifications.app.Models.Notification;
import com.Notifications.app.Services.Notifications.NotificationsRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("")
@Slf4j
@RequiredArgsConstructor
public class RestController {

    private final NotificationsRetrievalService retrievalService;

    @RequestMapping(value = "/getNotifications")
    public Page<Notification> getNotifications(@RequestParam int page, @RequestParam int pageSize) {
        return retrievalService.getUserNotifications(page,pageSize);
    }

}
