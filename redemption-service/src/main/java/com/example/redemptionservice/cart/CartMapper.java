package com.example.redemptionservice.cart;

import com.example.redemptionservice.cart.dto.AddCartItemRequest;
import com.example.redemptionservice.cart.dto.CartItemResponse;
import com.example.redemptionservice.cart.dto.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "redemptionCartItems", target = "items")
    @Mapping(target = "totalPoints", ignore = true)
    CartResponse toCartResponse(RedemptionCart cart);

    @Mapping(target = "rewardName", ignore = true)
    @Mapping(target = "pointsCost", ignore = true)
    @Mapping(target = "totalPoints", ignore = true)
    CartItemResponse toCartItemResponse(RedemptionCartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    RedemptionCartItem toEntity(AddCartItemRequest request);
}
