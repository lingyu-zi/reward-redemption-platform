package com.example.rewardredemption.activity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Data
@Table("customer_activity")
public class CustomerActivity {
    @PrimaryKey
    private CustomerActivityKey key;

    @Column("activity_type")
    private String activityType;

    @Column("total_points_cost")
    private Long totalPointsCost;

    @Column("redemption_id")
    private Long redemptionId;

    @Column("reward_ids")
    private List<Long> rewardIds;
}
