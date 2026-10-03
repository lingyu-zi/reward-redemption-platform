CREATE TABLE rewards_accounts
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT NOT NULL UNIQUE,
    points_balance  BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rewards_account_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id)
);