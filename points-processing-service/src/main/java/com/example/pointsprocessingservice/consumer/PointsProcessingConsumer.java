package com.example.pointsprocessingservice.consumer;


import com.example.pointsprocessingservice.event.TransactionCompletedEvent;
import com.example.pointsprocessingservice.service.PointsProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class PointsProcessingConsumer {
    private final PointsProcessingService pointsProcessingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transaction-completed",
            groupId = "points-processing-service")
    public void pointProcessing(String message) {
        var event = objectMapper.readValue(message, TransactionCompletedEvent.class);
        pointsProcessingService.processTransaction(event);
    }
}
