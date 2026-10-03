package com.example.rewardredemption.cart;

import com.example.rewardredemption.cart.dto.AddCartItemRequest;
import com.example.rewardredemption.cart.dto.CartItemResponse;
import com.example.rewardredemption.cart.dto.CartResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<CartResponse> createCart(
            @PathVariable Long customerId) {
        var cart = cartService.createCart(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @GetMapping("/{cartId}")
    public CartResponse getCartById(@PathVariable Long cartId){
        return cartService.getCartById(cartId);
    }

    @PostMapping("/{cartId}/items")
    public CartItemResponse addItemToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody AddCartItemRequest request){
        return cartService.addItemToCart(cartId, request);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long cartId, @PathVariable Long itemId){
        cartService.removeItemFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long cartId){
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
