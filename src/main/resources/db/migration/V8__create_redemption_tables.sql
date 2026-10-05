CREATE TABLE redemptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    total_points BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_redemptions_customer
     FOREIGN KEY (customer_id)
         REFERENCES customers(id)
);

CREATE TABLE redemption_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    redemption_id BIGINT NOT NULL,
    reward_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    points_cost BIGINT NOT NULL,

    CONSTRAINT fk_redemption_items_redemption
      FOREIGN KEY (redemption_id)
          REFERENCES redemptions(id),

    CONSTRAINT fk_redemption_items_reward
      FOREIGN KEY (reward_id)
          REFERENCES rewards(id),

    CONSTRAINT uq_redemption_reward
      UNIQUE (redemption_id, reward_id)
);
