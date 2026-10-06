package com.example.rewardredemption.activity;

import com.example.rewardredemption.activity.dto.CustomerActivityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerActivityMapper {
    @Mapping(source = "key.eventTime", target = "eventTime")
    CustomerActivityResponse toCustomerActivityResponse(CustomerActivity customerActivity);
}
