package com.example.rewardredemption.rewardsaccount;

import com.example.rewardredemption.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;


@Table(name = "rewards_accounts")
@Entity
@Getter
@Setter
public class RewardsAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(name = "points_balance", nullable = false)
    private Long pointsBalance;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
