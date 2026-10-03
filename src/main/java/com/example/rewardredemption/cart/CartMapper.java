package com.example.rewardredemption.cart;

import com.example.rewardredemption.cart.dto.AddCartItemRequest;
import com.example.rewardredemption.cart.dto.CartItemResponse;
import com.example.rewardredemption.cart.dto.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "redemptionCartItems", target = "items")
    CartResponse toCartResponse(RedemptionCart cart);

    @Mapping(source = "reward.id", target = "rewardId")
    @Mapping(source = "reward.name", target = "rewardName")
    @Mapping(source = "reward.pointsCost", target = "pointsCost")
    CartItemResponse toCartItemResponse(RedemptionCartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "reward", ignore = true)
    RedemptionCartItem toEntity(AddCartItemRequest request);
}
