CREATE TABLE rewards (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,

     name VARCHAR(150) NOT NULL,
     description VARCHAR(500),

     points_cost BIGINT NOT NULL,

     category_id BIGINT NOT NULL,
     merchant_id BIGINT NOT NULL,

     active BOOLEAN NOT NULL DEFAULT TRUE,

     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_rewards_category
         FOREIGN KEY (category_id)
             REFERENCES reward_categories(id),

     CONSTRAINT fk_rewards_merchant
         FOREIGN KEY (merchant_id)
             REFERENCES merchants(id)
);