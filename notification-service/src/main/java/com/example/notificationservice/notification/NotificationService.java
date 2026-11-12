package com.example.notificationservice.notification;

import com.example.notificationservice.event.RedemptionCompensatedEvent;
import com.example.notificationservice.event.RedemptionCompletedEvent;
import com.example.notificationservice.idempotency.ProcessedNotificationEvent;
import com.example.notificationservice.idempotency.ProcessedNotificationEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationService {
    private final ProcessedNotificationEventRepository processedNotificationEventRepository;

    @Transactional
    public void sendCompletionNotification(RedemptionCompletedEvent event) {
        if (processedNotificationEventRepository.existsByEventId(event.getEventId())){
            return ;
        }

        var pointsUsed = Math.abs(event.getPointsChange());

        log.info(
                "Customer {} redeemed rewards {}. Points used: {}. Remaining balance: {}",
                event.getCustomerId(),
                event.getRewardIds(),
                pointsUsed,
                event.getRemainingPointsBalance()
        );

        var processedEvent = new ProcessedNotificationEvent();
        processedEvent.setEventId(event.getEventId());

        processedNotificationEventRepository.save(processedEvent);
    }

    public void sendCompensationNotification(RedemptionCompensatedEvent event) {
        if (processedNotificationEventRepository.existsByEventId(event.getEventId())){
            return ;
        }

        log.info(
                "Customer {} had {} points restored for redemption {}. Remaining balance: {}",
                event.getCustomerId(),
                event.getPointsReturned(),
                event.getRedemptionId(),
                event.getRemainingPointsBalance()
        );

        var processedEvent = new ProcessedNotificationEvent();
        processedEvent.setEventId(event.getEventId());

        processedNotificationEventRepository.save(processedEvent);
    }
}
