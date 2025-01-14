package com.commentsManager.app.Services.Notifications;

import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Models.EnvironmentVariables;
import com.commentsManager.app.Models.Records.Account;
import com.commentsManager.app.Models.Records.NotificationRecord;
import com.commentsManager.app.Services.API.AuthServiceAPIService;
import com.commentsManager.app.Services.JsonUtils;
import com.commentsManager.app.Services.Kafka.KafkaSenderService;
import com.commentsManager.app.Services.Notifications.Interfaces.NotificationGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationGeneratorService implements NotificationGenerator {

    private final AuthServiceAPIService apiService;

    private final KafkaSenderService kafkaSenderService;

    private void sendNotification(NotificationRecord notification){
        String json = JsonUtils.toJson(notification);
        kafkaSenderService.send("new_notification",json);
    };
}
