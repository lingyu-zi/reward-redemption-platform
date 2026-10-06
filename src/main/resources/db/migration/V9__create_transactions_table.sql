CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_reference BINARY(16) NOT NULL,
    customer_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id),

    CONSTRAINT fk_transactions_merchant
        FOREIGN KEY (merchant_id)
            REFERENCES merchants(id),

    CONSTRAINT uq_transactions_reference
        UNIQUE (transaction_reference)
);