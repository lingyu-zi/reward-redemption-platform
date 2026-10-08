package com.example.activityhistoryservice.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@PrimaryKeyClass
public class CustomerActivityKey {
    @PrimaryKeyColumn(name = "customer_id",
            type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    private Long customerId;

    @PrimaryKeyColumn(name = "event_time", type = PrimaryKeyType.CLUSTERED,
            ordinal = 1, ordering = Ordering.DESCENDING)
    private Instant eventTime;

    @PrimaryKeyColumn(name = "activity_id", type = PrimaryKeyType.CLUSTERED,
            ordinal = 2, ordering = Ordering.DESCENDING)
    private UUID activityId;
}
