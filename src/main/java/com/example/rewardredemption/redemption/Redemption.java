package com.example.rewardredemption.redemption;

import com.example.rewardredemption.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "redemptions")
public class Redemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RedemptionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "redemption", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RedemptionItem> redemptionItems = new LinkedHashSet<>();
}