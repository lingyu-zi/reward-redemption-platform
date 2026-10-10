package com.example.redemptionservice.redemption;

import com.example.redemptionservice.redemption.dto.RedemptionItemResponse;
import com.example.redemptionservice.redemption.dto.RedemptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RedemptionMapper {
    @Mapping(source = "redemptionItems", target = "items")
    RedemptionResponse toRedemptionResponse(Redemption redemption);

    @Mapping(target = "rewardName", ignore = true)
    RedemptionItemResponse toRedemptionItemResponse(RedemptionItem redemptionItem);
}
