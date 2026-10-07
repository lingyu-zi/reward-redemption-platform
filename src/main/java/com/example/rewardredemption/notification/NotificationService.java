package com.example.rewardredemption.notification;

import com.example.rewardredemption.redemption.event.RedemptionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendRedemptionNotification(RedemptionCompletedEvent event) {
        var pointsUsed = Math.abs(event.getPointsChange());

        log.info(
                "Customer {} redeemed rewards {}. Points used: {}. Remaining balance: {}",
                event.getCustomerId(),
                event.getRewardIds(),
                pointsUsed,
                event.getRemainingPointsBalance()
        );
    }
}
