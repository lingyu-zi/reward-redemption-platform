CREATE TABLE reward_activity.customer_activity (
   customer_id bigint,
   event_time timestamp,
   activity_id timeuuid,
   activity_type text,
   reward_ids list<bigint>,
   redemption_id bigint,

   PRIMARY KEY (
       (customer_id),
        event_time,
        activity_id
       )
)
    WITH CLUSTERING ORDER BY (
        event_time DESC,
        activity_id DESC
);