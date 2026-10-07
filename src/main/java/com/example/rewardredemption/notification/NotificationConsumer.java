package com.example.rewardredemption.notification;

import com.example.rewardredemption.redemption.event.RedemptionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.management.Notification;

@RequiredArgsConstructor
@Component
public class NotificationConsumer {
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "redemption-completed",
            groupId = "notification-group"
    )
    public void sendRedemptionNotification(RedemptionCompletedEvent event) {
        notificationService.sendRedemptionNotification(event);
    }
}
