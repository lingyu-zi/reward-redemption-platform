package com.example.rewardredemption.rewardsaccount;

import com.example.rewardredemption.rewardsaccount.dto.RewardsAccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RewardsAccountMapper {
    @Mapping(source = "customer.id", target = "customerId")
    RewardsAccountResponse toResponse(RewardsAccount account);
}
