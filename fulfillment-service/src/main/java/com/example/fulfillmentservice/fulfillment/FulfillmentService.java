package com.example.fulfillmentservice.fulfillment;

import com.example.fulfillmentservice.event.RedemptionCompletedEvent;
import com.example.fulfillmentservice.event.RedemptionFailedEvent;
import com.example.fulfillmentservice.event.RedemptionFailureProducer;
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
    private final RedemptionFailureProducer redemptionFailureProducer;

    @Transactional
    public void fulfill(RedemptionCompletedEvent event){
        var fulfillmentRecord = fulfillmentRecordRepository.findByRedemptionId(
                event.getRedemptionId()).orElseGet(() -> createFulfillmentRecord(event));

        if (fulfillmentRecord.getStatus() == FulfillmentStatus.SUCCESS) {
            return;
        }

        fulfillmentRecord.setStatus(FulfillmentStatus.PENDING);
        fulfillmentRecord.setAttemptCount(fulfillmentRecord.getAttemptCount() + 1);

        try {
            merchantProviderClient.fulfill(event);

            fulfillmentRecord.setStatus(FulfillmentStatus.SUCCESS);
            fulfillmentRecord.setFailureReason(null);
        } catch (Exception e) {
            fulfillmentRecord.setStatus(FulfillmentStatus.FAILED);
            var reason = e.getMessage() != null ?
                    e.getMessage() : "Merchant fulfillment failed";
            fulfillmentRecord.setFailureReason(reason);

            var failedEvent = new RedemptionFailedEvent();
            failedEvent.setEventId(UUID.randomUUID());
            failedEvent.setOccurredAt(Instant.now());
            failedEvent.setCustomerId(event.getCustomerId());
            failedEvent.setRedemptionId(event.getRedemptionId());
            failedEvent.setPointsToReturn(-event.getPointsChange());
            failedEvent.setReason(reason);

            redemptionFailureProducer.publish(failedEvent);
        }
        fulfillmentRecordRepository.save(fulfillmentRecord);
    }

    private FulfillmentRecord createFulfillmentRecord(
            RedemptionCompletedEvent event){
        var record = new FulfillmentRecord();

        record.setRedemptionId(event.getRedemptionId());
        record.setCustomerId(event.getCustomerId());
        record.setStatus(FulfillmentStatus.PENDING);
        record.setAttemptCount(0);

        return record;
    }
}
