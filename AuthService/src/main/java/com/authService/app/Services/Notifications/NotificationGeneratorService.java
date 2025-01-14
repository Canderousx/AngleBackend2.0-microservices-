package com.authService.app.Services.Notifications;
import com.authService.app.Models.Records.NotificationRecord;
import com.authService.app.Services.JsonUtils;
import com.authService.app.Services.Kafka.KafkaSenderService;
import com.authService.app.Services.Notifications.Interfaces.NotificationGenerator;
import org.springframework.stereotype.Service;

@Service
public class NotificationGeneratorService implements NotificationGenerator {

    private final KafkaSenderService kafkaSenderService;

    public NotificationGeneratorService(KafkaSenderService kafkaSenderService) {
        this.kafkaSenderService = kafkaSenderService;
    }

    private void sendNotification(NotificationRecord notification){
        String json = JsonUtils.toJson(notification);
        kafkaSenderService.send("new_notification",json);
    };

    @Override
    public void newSubscriberNotification(String ownerId, String subscriberName, String subscriberId) {
        NotificationRecord note = new NotificationRecord(
                ownerId,
                subscriberName+" has subscribed your channel!",
                "",
                "/api/auth/accounts/media/getAvatar?userId="+subscriberId,
                "/channel?id="+subscriberId,
                true
        );
        sendNotification(note);
    }
}
