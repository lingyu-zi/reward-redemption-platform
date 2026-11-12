package com.example.fulfillmentservice.fulfillment;

import com.example.fulfillmentservice.event.*;
import com.example.fulfillmentservice.merchant.MerchantProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FulfillmentService {
    private final FulfillmentRecordRepository fulfillmentRecordRepository;
    private final MerchantProviderClient merchantProviderClient;
    private final FulfillmentFailedProducer fulfillmentFailedProducer;
    private final FulfillmentSucceededProducer fulfillmentSucceededProducer;

    @Transactional
    public void fulfill(RedemptionRequestedEvent event){
        var fulfillmentRecord = fulfillmentRecordRepository.findByRedemptionId(
                event.getRedemptionId()).orElseGet(() -> createFulfillmentRecord(event));

        if (fulfillmentRecord.getStatus() == FulfillmentStatus.SUCCESS) {
            return;
        }

        fulfillmentRecord.setStatus(FulfillmentStatus.PENDING);
        fulfillmentRecord.setAttemptCount(fulfillmentRecord.getAttemptCount() + 1);

        try {
            merchantProviderClient.fulfill(event);

        } catch (Exception e) {
            fulfillmentRecord.setStatus(FulfillmentStatus.FAILED);
            var reason = e.getMessage() != null ?
                    e.getMessage() : "Merchant fulfillment failed";
            fulfillmentRecord.setFailureReason(reason);

            fulfillmentRecordRepository.save(fulfillmentRecord);

            var failedEvent = new FulfillmentFailedEvent();
            failedEvent.setEventId(UUID.randomUUID());
            failedEvent.setOccurredAt(Instant.now());
            failedEvent.setCustomerId(event.getCustomerId());
            failedEvent.setRedemptionId(event.getRedemptionId());
            failedEvent.setPointsToReturn(-event.getPointsChange());
            failedEvent.setReason(reason);

            fulfillmentFailedProducer.publishFulfillmentFailed(failedEvent);

            return;
        }

        fulfillmentRecord.setStatus(FulfillmentStatus.SUCCESS);
        fulfillmentRecord.setFailureReason(null);

        fulfillmentRecordRepository.save(fulfillmentRecord);

        var succeededEvent = new FulfillmentSucceededEvent();
        succeededEvent.setEventId(UUID.randomUUID());
        succeededEvent.setRedemptionId(event.getRedemptionId());
        succeededEvent.setCustomerId(event.getCustomerId());
        succeededEvent.setRewardIds(event.getRewardIds());
        succeededEvent.setPointsChange(event.getPointsChange());
        succeededEvent.setRemainingPointsBalance(event.getRemainingPointsBalance());
        succeededEvent.setOccurredAt(Instant.now());

        fulfillmentSucceededProducer.publishFulfillmentSucceeded(succeededEvent);
    }

    private FulfillmentRecord createFulfillmentRecord(
            RedemptionRequestedEvent event){
        var record = new FulfillmentRecord();

        record.setRedemptionId(event.getRedemptionId());
        record.setCustomerId(event.getCustomerId());
        record.setStatus(FulfillmentStatus.PENDING);
        record.setAttemptCount(0);

        return record;
    }
}
