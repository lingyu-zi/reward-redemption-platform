CREATE TABLE processed_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id BINARY(16) NOT NULL,
  consumer_name VARCHAR(100) NOT NULL,
  processed_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT uk_processed_event_consumer
      UNIQUE (event_id, consumer_name)
);