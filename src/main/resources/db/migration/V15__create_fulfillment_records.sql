CREATE TABLE fulfillment_records (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,

     redemption_id BIGINT NOT NULL,
     customer_id BIGINT NOT NULL,

     status VARCHAR(50) NOT NULL,

     failure_reason VARCHAR(500) NULL,
     attempt_count INT NOT NULL DEFAULT 0,

     created_at TIMESTAMP(6)
         NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

     updated_at TIMESTAMP(6)
         NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
         ON UPDATE CURRENT_TIMESTAMP(6),

     CONSTRAINT uk_fulfillment_redemption
         UNIQUE (redemption_id)
);