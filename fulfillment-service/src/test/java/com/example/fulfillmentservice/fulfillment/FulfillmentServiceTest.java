package com.example.fulfillmentservice.fulfillment;

import com.example.fulfillmentservice.event.RedemptionCompletedEvent;
import com.example.fulfillmentservice.event.RedemptionFailedEvent;
import com.example.fulfillmentservice.event.RedemptionFailureProducer;
import com.example.fulfillmentservice.merchant.MerchantProviderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FulfillmentServiceTest {
    @Mock
    private FulfillmentRecordRepository fulfillmentRecordRepository;

    @Mock
    private MerchantProviderClient merchantProviderClient;

    @Mock
    private RedemptionFailureProducer redemptionFailureProducer;

    @InjectMocks
    private FulfillmentService fulfillmentService;

    @Test
    void shouldMarkFulfillmentSuccessfulWhenMerchantSucceeds() {
        var event = createEvent();

        var fulfillmentRecord = createPendingFulfillmentRecord(event);

        when(fulfillmentRecordRepository.findByRedemptionId(
                event.getRedemptionId()))
                .thenReturn(Optional.of(fulfillmentRecord));


        fulfillmentService.fulfill(event);

        assertEquals(1, fulfillmentRecord.getAttemptCount());
        assertEquals(FulfillmentStatus.SUCCESS, fulfillmentRecord.getStatus());
        assertNull(fulfillmentRecord.getFailureReason());

        verify(fulfillmentRecordRepository)
                .findByRedemptionId(event.getRedemptionId());
        verify(merchantProviderClient).fulfill(event);
        verify(fulfillmentRecordRepository).save(fulfillmentRecord);
        verify(redemptionFailureProducer, never()).publish(any());
    }

    @Test
    void shouldPublishFailureEventWhenMerchantFails() {
        var event = createEvent();

        var fulfillmentRecord = createPendingFulfillmentRecord(event);

        when(fulfillmentRecordRepository.findByRedemptionId(
                event.getRedemptionId()))
                .thenReturn(Optional.of(fulfillmentRecord));

        doThrow(new RuntimeException("Merchant provider unavailable"))
                .when(merchantProviderClient).fulfill(event);

        fulfillmentService.fulfill(event);

        assertEquals(1, fulfillmentRecord.getAttemptCount());
        assertEquals(FulfillmentStatus.FAILED, fulfillmentRecord.getStatus());
        assertEquals("Merchant provider unavailable",
                fulfillmentRecord.getFailureReason());

        var captor = ArgumentCaptor.forClass(RedemptionFailedEvent.class);

        verify(fulfillmentRecordRepository)
                .findByRedemptionId(event.getRedemptionId());
        verify(fulfillmentRecordRepository).save(fulfillmentRecord);
        verify(redemptionFailureProducer).publish(captor.capture());
        verify(merchantProviderClient).fulfill(event);

        var failedEvent = captor.getValue();

        assertEquals(event.getRedemptionId(), failedEvent.getRedemptionId());
        assertEquals(event.getCustomerId(), failedEvent.getCustomerId());
        assertEquals(-event.getPointsChange(), failedEvent.getPointsToReturn());

        assertEquals("Merchant provider unavailable",
                failedEvent.getReason());

        assertNotNull(failedEvent.getEventId());
        assertNotNull(failedEvent.getOccurredAt());
    }

    @Test
    void shouldCreateNewFulfillmentRecordWhenNoneExists() {
        var event = createEvent();

        when(fulfillmentRecordRepository.findByRedemptionId(
                event.getRedemptionId()))
                .thenReturn(Optional.empty());

        fulfillmentService.fulfill(event);

        var captor = ArgumentCaptor.forClass(FulfillmentRecord.class);

        verify(fulfillmentRecordRepository).save(captor.capture());

        var savedRecord = captor.getValue();

        assertEquals(event.getRedemptionId(), savedRecord.getRedemptionId());
        assertEquals(event.getCustomerId(), savedRecord.getCustomerId());
        assertEquals(FulfillmentStatus.SUCCESS, savedRecord.getStatus());
        assertEquals(1, savedRecord.getAttemptCount());
        assertNull(savedRecord.getFailureReason());

        verify(merchantProviderClient).fulfill(event);
        verify(redemptionFailureProducer, never()).publish(any());
    }

    @Test
    void shouldIgnoreDuplicateEventWhenFulfillmentAlreadySucceeded() {
        var event = createEvent();

        var fulfillmentRecord = createPendingFulfillmentRecord(event);
        fulfillmentRecord.setStatus(FulfillmentStatus.SUCCESS);

        when(fulfillmentRecordRepository.findByRedemptionId(
                event.getRedemptionId()))
                .thenReturn(Optional.of(fulfillmentRecord));

        fulfillmentService.fulfill(event);

        assertEquals(FulfillmentStatus.SUCCESS, fulfillmentRecord.getStatus());
        assertEquals(0, fulfillmentRecord.getAttemptCount());

        verify(fulfillmentRecordRepository)
                .findByRedemptionId(event.getRedemptionId());

        verify(fulfillmentRecordRepository, never())
                .save(any(FulfillmentRecord.class));

        verify(redemptionFailureProducer, never()).publish(any());
        verify(merchantProviderClient, never()).fulfill(any());
    }

    private FulfillmentRecord createPendingFulfillmentRecord(RedemptionCompletedEvent event) {
        var fulfillmentRecord = new FulfillmentRecord();
        fulfillmentRecord.setRedemptionId(event.getRedemptionId());
        fulfillmentRecord.setCustomerId(event.getCustomerId());
        fulfillmentRecord.setStatus(FulfillmentStatus.PENDING);
        fulfillmentRecord.setAttemptCount(0);
        return fulfillmentRecord;
    }

    private RedemptionCompletedEvent createEvent() {
        var event = new RedemptionCompletedEvent();
        event.setCustomerId(1L);
        event.setRedemptionId(1L);
        event.setPointsChange(-100L);
        return event;
    }
}
