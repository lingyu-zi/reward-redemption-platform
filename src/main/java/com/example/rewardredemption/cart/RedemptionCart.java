package com.example.rewardredemption.cart;

import com.example.rewardredemption.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "redemption_carts")
public class RedemptionCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RedemptionCartItem> redemptionCartItems = new LinkedHashSet<>();

    public Long getTotalPoints(){
        return redemptionCartItems.stream().
                mapToLong(RedemptionCartItem::getTotalPoints).sum();
    }

    public void clearCart(){
        redemptionCartItems.clear();
    }
}