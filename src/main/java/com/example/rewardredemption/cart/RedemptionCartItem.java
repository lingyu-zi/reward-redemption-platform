package com.example.rewardredemption.cart;

import com.example.rewardredemption.cart.dto.AddCartItemRequest;
import com.example.rewardredemption.reward.Reward;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "redemption_cart_items")
public class RedemptionCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private RedemptionCart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public Long getTotalPoints(){
        return reward.getPointsCost() * quantity;
    }

}