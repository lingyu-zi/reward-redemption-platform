CREATE TABLE processed_notification_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    event_id BINARY(16) NOT NULL,

    processed_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT uk_processed_notification_event
        UNIQUE (event_id)
);