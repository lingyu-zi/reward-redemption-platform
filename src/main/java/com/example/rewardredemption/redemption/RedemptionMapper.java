package com.example.rewardredemption.redemption;

import com.example.rewardredemption.redemption.dto.RedemptionItemResponse;
import com.example.rewardredemption.redemption.dto.RedemptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RedemptionMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "redemptionItems", target = "items")
    RedemptionResponse toRedemptionResponse(Redemption redemption);

    @Mapping(source = "reward.id", target = "rewardId")
    @Mapping(source = "reward.name", target = "rewardName")
    RedemptionItemResponse toRedemptionItemResponse(RedemptionItem redemptionItem);
}
