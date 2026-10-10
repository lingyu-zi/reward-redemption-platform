package com.example.redemptionservice.cart;

import jakarta.persistence.*;
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

    @Column(name = "reward_id", nullable = false)
    private Long rewardId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public void increaseQuantity(Integer quantity) {
        this.quantity += quantity;
    }
}