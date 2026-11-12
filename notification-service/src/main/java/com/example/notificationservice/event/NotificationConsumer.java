package com.example.notificationservice.event;

import com.example.notificationservice.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    // Redemption completed
    @KafkaListener(
            topics = "redemption-completed",
            groupId = "notification-service",
            properties = {
                    "spring.json.value.default.type=com.example.notificationservice.event.RedemptionCompletedEvent"
            }
    )
    public void sendCompletionNotification(RedemptionCompletedEvent event) {
        notificationService.sendCompletionNotification(event);
    }


    // Redemption compensated
    @KafkaListener(
            topics = "redemption-compensated",
            groupId = "notification-service",
            properties = {
                    "spring.json.value.default.type=com.example.notificationservice.event.RedemptionCompensatedEvent"
            }
    )
    public void sendCompensationNotification(RedemptionCompensatedEvent event) {
        notificationService.sendCompensationNotification(event);
    }


}
