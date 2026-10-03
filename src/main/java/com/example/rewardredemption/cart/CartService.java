package com.example.rewardredemption.cart;

import com.example.rewardredemption.cart.dto.AddCartItemRequest;
import com.example.rewardredemption.cart.dto.CartItemResponse;
import com.example.rewardredemption.cart.dto.CartResponse;
import com.example.rewardredemption.customer.CustomerRepository;
import com.example.rewardredemption.exception.DuplicateResourceException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.reward.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartService {
    private final RedemptionCartRepository redemptionCartRepository;
    private final RedemptionCartItemRepository redemptionCartItemRepository;
    private final CustomerRepository customerRepository;
    private final CartMapper cartMapper;
    private final RewardRepository rewardRepository;

    @Transactional
    public CartResponse createCart(Long customerId){
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found by id: " + customerId));
        if (redemptionCartRepository.existsByCustomerId(customerId)){
            throw new DuplicateResourceException(
                    "Customer already has a redemption cart");
        }
        RedemptionCart redemptionCart = new RedemptionCart();
        redemptionCart.setCustomer(customer);
        var savedCart = redemptionCartRepository.save(redemptionCart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Transactional
    public CartResponse getCartById(Long cartId){
        var cart = redemptionCartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Cart not found by id: " + cartId));
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartItemResponse addItemToCart(Long cartId, AddCartItemRequest request){
        var cart = redemptionCartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Cart not found by id: " + cartId));
        var reward = rewardRepository.findById(request.getRewardId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Reward not found by id: " + request.getRewardId()));
        var existingItem = redemptionCartItemRepository
                .findByCartIdAndRewardId(cartId, request.getRewardId());
        RedemptionCartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new RedemptionCartItem();
            cartItem.setCart(cart);
            cartItem.setReward(reward);
            cartItem.setQuantity(request.getQuantity());
        }
        var savedCartItem = redemptionCartItemRepository.save(cartItem);
        return cartMapper.toCartItemResponse(savedCartItem);
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
}
