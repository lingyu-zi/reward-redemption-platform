package com.example.redemptionservice.cart;

import com.example.redemptionservice.cart.dto.AddCartItemRequest;
import com.example.redemptionservice.cart.dto.CartItemResponse;
import com.example.redemptionservice.cart.dto.CartResponse;
import com.example.redemptionservice.exception.BadRequestException;
import com.example.redemptionservice.exception.DuplicateResourceException;
import com.example.redemptionservice.exception.ResourceNotFoundException;
import com.example.redemptionservice.reward.Reward;
import com.example.redemptionservice.reward.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartService {
    private final RedemptionCartRepository redemptionCartRepository;
    private final RedemptionCartItemRepository redemptionCartItemRepository;
    private final RewardRepository rewardRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartResponse createCart(Long customerId){
        if (redemptionCartRepository.existsByCustomerId(customerId)){
            throw new DuplicateResourceException(
                    "Customer already has a redemption cart");
        }
        RedemptionCart redemptionCart = new RedemptionCart();
        redemptionCart.setCustomerId(customerId);
        var savedCart = redemptionCartRepository.save(redemptionCart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Transactional
    public CartResponse getCartById(Long cartId){
        var cart = redemptionCartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Cart with id: " + cartId + " not found"));
        var rewardIds = cart.getRedemptionCartItems().stream()
                .map(RedemptionCartItem::getRewardId)
                .toList();
        // load all rewards in one query - avoid N+1 problem
        var rewardMap = rewardRepository.findAllById(rewardIds).stream()
                .collect(Collectors.toMap(Reward::getId, reward -> reward));
        var items = cart.getRedemptionCartItems().stream()
                .map(item -> buildCartItemResponseFromMap(item, rewardMap))
                .toList();
        var response = cartMapper.toCartResponse(cart);
        response.setItems(items);
        response.setTotalPoints(items.stream()
                .mapToLong(CartItemResponse::getTotalPoints)
                .sum());
        return response;
    }

    @Transactional
    public CartItemResponse addItemToCart(Long cartId, AddCartItemRequest request){
        var cart = redemptionCartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Cart not found by id: " + cartId));
        var reward = rewardRepository.findById(request.getRewardId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Reward not found by id: " + request.getRewardId()));
        if (!reward.getActive()) {
            throw new BadRequestException("Reward is not active");
        }
        var existingItem = redemptionCartItemRepository
                .findByCartIdAndRewardId(cartId, request.getRewardId());
        RedemptionCartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.increaseQuantity(request.getQuantity());
        } else {
            cartItem = new RedemptionCartItem();
            cartItem.setCart(cart);
            cartItem.setRewardId(request.getRewardId());
            cartItem.setQuantity(request.getQuantity());
        }
        var savedCartItem = redemptionCartItemRepository.save(cartItem);
        return buildCartItemResponse(savedCartItem, reward);
    }

    @Transactional
    public void removeItemFromCart(Long cartId, Long itemId){
        var item = redemptionCartItemRepository
                .findByCartIdAndId(cartId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found by id: " + itemId));
        redemptionCartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long cartId){
        var cart = redemptionCartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Cart not found by id: " + cartId));
        cart.clearCart();
    }

    private CartItemResponse buildCartItemResponseFromMap(
            RedemptionCartItem item,
            Map<Long, Reward> rewardMap){
        var reward = rewardMap.get(item.getRewardId());
        if (reward == null) {
            throw new ResourceNotFoundException(
                    "Reward not found by id: " + item.getRewardId());
        }
        return buildCartItemResponse(item, reward);
    }

    private CartItemResponse buildCartItemResponse(
            RedemptionCartItem item,
            Reward reward) {
        var response = cartMapper.toCartItemResponse(item);
        response.setRewardName(reward.getName());
        response.setPointsCost(reward.getPointsCost());
        response.setTotalPoints(
                reward.getPointsCost() * item.getQuantity());
        return response;
    }
}
