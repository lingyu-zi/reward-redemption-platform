package com.example.rewardredemption.redemption;

import com.example.rewardredemption.cart.RedemptionCartRepository;
import com.example.rewardredemption.exception.BadRequestException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.redemption.dto.RedemptionResponse;
import com.example.rewardredemption.rewardsaccount.RewardsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RedemptionService {
    private final RedemptionRepository redemptionRepository;
    private final RedemptionCartRepository redemptionCartRepository;
    private final RewardsAccountRepository rewardsAccountRepository;
    private final RedemptionMapper redemptionMapper;

    @Transactional
    public RedemptionResponse redeem(Long cartId) {
        // get customer's cart
        var cart = redemptionCartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Redemption cart not found with id: " + cartId));
        // check if cart is empty
        if (cart.getRedemptionCartItems().isEmpty()) {
            throw new BadRequestException(
                    "Cannot redeem an empty cart");
        }
        // calculate total points
        var totalPoints = cart.getTotalPoints();
        // get reward account
        var rewardsAccount = rewardsAccountRepository.findByCustomerId(
                cart.getCustomer().getId()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Rewards account not found for customer id: "
                                        + cart.getCustomer().getId()));
        // check if enough points
        if (rewardsAccount.getPointsBalance() < totalPoints) {
            throw new BadRequestException(
                    "Insufficient points to complete redemption");
        }
        Redemption redemption = new Redemption();
        redemption.setCustomer(cart.getCustomer());
        redemption.setTotalPoints(totalPoints);
        redemption.setStatus(RedemptionStatus.PENDING);
        var savedRedemption = redemptionRepository.save(redemption);
        // copy cart items
        for (var cartItem : cart.getRedemptionCartItems()) {
            RedemptionItem redemptionItem = new RedemptionItem();
            redemptionItem.setRedemption(savedRedemption);
            redemptionItem.setReward(cartItem.getReward());
            redemptionItem.setQuantity(cartItem.getQuantity());
            redemptionItem.setPointsCost(cartItem.getReward().getPointsCost());
            savedRedemption.getRedemptionItems().add(redemptionItem);
        }
        // deduct points
        rewardsAccount.setPointsBalance(rewardsAccount.getPointsBalance() - totalPoints);
        // clear cart
        cart.clearCart();
        savedRedemption.setStatus(RedemptionStatus.COMPLETED);
        redemptionRepository.flush();
        return redemptionMapper.toRedemptionResponse(savedRedemption);
    }

    public RedemptionResponse getRedemptionById(Long redemptionId) {
        var redemption =  redemptionRepository.findById(redemptionId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Redemption not found with id: " + redemptionId));
        return redemptionMapper.toRedemptionResponse(redemption);
    }

    public List<RedemptionResponse> getRedemptionsByCustomerId(Long customerId) {
        return redemptionRepository.findByCustomerId(customerId).stream()
                .map(redemptionMapper::toRedemptionResponse)
                .toList();
    }

}
