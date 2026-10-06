package com.example.rewardredemption.activity;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerActivityRepository
        extends CassandraRepository<CustomerActivity, CustomerActivityKey> {
    List<CustomerActivity> findByKeyCustomerId(Long customerId);
}
