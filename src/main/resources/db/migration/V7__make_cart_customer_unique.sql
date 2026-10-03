ALTER TABLE redemption_carts
    ADD CONSTRAINT uq_redemption_carts_customer
        UNIQUE (customer_id);