package com.example.notificationservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "redemption-completed",
            groupId = "notification-service"
    )
    public void sendRedemptionNotification(RedemptionCompletedEvent event) {
        notificationService.sendRedemptionNotification(event);
    }
}
