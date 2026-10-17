package com.example.notificationservice.event;

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
    public void sendRedemptionNotification(RedemptionCompletedEvent event) {
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
}
