CREATE TABLE redemption_compensations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,

  event_id BINARY(16) NOT NULL,
  redemption_id BIGINT NOT NULL,
  customer_id BIGINT NOT NULL,

  points_returned BIGINT NOT NULL,

  status VARCHAR(50) NOT NULL,

  created_at TIMESTAMP(6)
      NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT uk_redemption_compensation_event
      UNIQUE (event_id),

  CONSTRAINT uk_redemption_compensation_redemption
      UNIQUE (redemption_id)
);