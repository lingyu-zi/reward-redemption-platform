package com.example.rewardredemption.merchant;

import com.example.rewardredemption.merchant.dto.CreateMerchantRequest;
import com.example.rewardredemption.merchant.dto.MerchantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    MerchantResponse toResponse(Merchant merchant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Merchant toEntity(CreateMerchantRequest request);
}
