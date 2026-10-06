CREATE TABLE redemption_carts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_redemption_carts_customer
      FOREIGN KEY (customer_id)
          REFERENCES customers(id)
);

CREATE TABLE redemption_cart_items (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   cart_id BIGINT NOT NULL,
   reward_id BIGINT NOT NULL,
   quantity INT NOT NULL,

   CONSTRAINT fk_cart_items_cart
       FOREIGN KEY (cart_id)
           REFERENCES redemption_carts(id),

   CONSTRAINT fk_cart_items_reward
       FOREIGN KEY (reward_id)
           REFERENCES rewards(id),

   CONSTRAINT uq_cart_reward
       UNIQUE (cart_id, reward_id)
);