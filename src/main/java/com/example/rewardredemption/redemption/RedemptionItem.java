package com.example.rewardredemption.redemption;

import com.example.rewardredemption.reward.Reward;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "redemption_items")
public class RedemptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "redemption_id", nullable = false)
    private Redemption redemption;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "points_cost", nullable = false)
    private Long pointsCost;

    public Long getTotalPoints() {
        return quantity * pointsCost;
    }
}