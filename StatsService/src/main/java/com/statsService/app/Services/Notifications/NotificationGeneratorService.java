package com.statsService.app.Services.Notifications;

import com.statsService.app.Models.Records.NotificationRecord;
import com.statsService.app.Services.JsonUtils;
import com.statsService.app.Services.Kafka.KafkaSenderService;
import com.statsService.app.Services.Notifications.Interfaces.NotificationGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationGeneratorService implements NotificationGenerator {

    private final KafkaSenderService kafkaSenderService;
    private void sendNotification(NotificationRecord notification){
        String json = JsonUtils.toJson(notification);
        kafkaSenderService.send("new_notification",json);
    };


}
