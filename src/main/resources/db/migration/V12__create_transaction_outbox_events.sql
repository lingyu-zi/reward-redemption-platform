CREATE TABLE outbox_events (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,

   event_id BINARY(16) NOT NULL,
   event_type VARCHAR(100) NOT NULL,

   payload TEXT NOT NULL,

   published BOOLEAN NOT NULL DEFAULT FALSE,

   created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
   published_at TIMESTAMP(6) NULL,

   CONSTRAINT uk_outbox_event_id UNIQUE (event_id)
);