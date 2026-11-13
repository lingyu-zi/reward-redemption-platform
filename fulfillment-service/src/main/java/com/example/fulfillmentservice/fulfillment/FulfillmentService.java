package com.example.fulfillmentservice.fulfillment;

import com.example.fulfillmentservice.event.*;
import com.example.fulfillmentservice.merchant.MerchantProviderClient;
import com.example.fulfillmentservice.merchant.exception.MerchantProviderTimeoutException;
import com.example.fulfillmentservice.merchant.exception.MerchantProviderUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.UUID;

@Slf4j
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
            var failureReason = getFailureReason(e);

            log.error(
                    "Merchant fulfillment failed: redemptionId={}, reason={}",
                    event.getRedemptionId(),
                    failureReason,
                    e
            );

            fulfillmentRecord.setStatus(FulfillmentStatus.FAILED);
            fulfillmentRecord.setFailureReason(failureReason);

            fulfillmentRecordRepository.save(fulfillmentRecord);

            var failedEvent = new FulfillmentFailedEvent();
            failedEvent.setEventId(UUID.randomUUID());
            failedEvent.setOccurredAt(Instant.now());
            failedEvent.setCustomerId(event.getCustomerId());
            failedEvent.setRedemptionId(event.getRedemptionId());
            failedEvent.setPointsToReturn(-event.getPointsChange());
            failedEvent.setReason(failureReason);

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

    private String getFailureReason(Exception ex){
        if (ex instanceof MerchantProviderTimeoutException) {
            return "Merchant provider timed out";
        }

        if (ex instanceof MerchantProviderUnavailableException) {
            return "Merchant provider unavailable";
        }

        if (ex instanceof CallNotPermittedException) {
            return "Merchant provider circuit breaker is open";
        }

        if (ex instanceof HttpClientErrorException.Conflict) {
            return "Merchant provider out of stock";
        }

        if (ex instanceof HttpClientErrorException.BadRequest) {
            return "Merchant provider rejected the request";
        }

        return "Merchant fulfillment failed";
    }
}
