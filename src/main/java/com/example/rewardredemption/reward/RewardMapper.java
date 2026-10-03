package com.example.rewardredemption.reward;

import com.example.rewardredemption.reward.dto.CreateRewardRequest;
import com.example.rewardredemption.reward.dto.RewardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RewardMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "merchant.id", target = "merchantId")
    @Mapping(source = "merchant.name", target = "merchantName")
    RewardResponse toResponse(Reward reward);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reward toEntity(CreateRewardRequest request);
}
