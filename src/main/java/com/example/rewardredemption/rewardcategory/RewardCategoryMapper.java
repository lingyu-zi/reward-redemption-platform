package com.example.rewardredemption.rewardcategory;

import com.example.rewardredemption.rewardcategory.dto.CreateRewardCategoryRequest;
import com.example.rewardredemption.rewardcategory.dto.RewardCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RewardCategoryMapper {
    RewardCategoryResponse toResponse(RewardCategory rewardCategory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RewardCategory toEntity(CreateRewardCategoryRequest request);
}
