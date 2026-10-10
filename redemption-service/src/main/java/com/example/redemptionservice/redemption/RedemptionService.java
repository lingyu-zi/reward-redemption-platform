package com.example.redemptionservice.redemption;

import com.example.redemptionservice.account.RewardsAccountRepository;
import com.example.redemptionservice.cart.RedemptionCartItem;
import com.example.redemptionservice.cart.RedemptionCartRepository;
import com.example.redemptionservice.event.RedemptionCompletedEvent;
import com.example.redemptionservice.event.RedemptionEventProducer;
import com.example.redemptionservice.exception.BadRequestException;
import com.example.redemptionservice.exception.ResourceNotFoundException;
import com.example.redemptionservice.redemption.dto.RedemptionResponse;
import com.example.redemptionservice.reward.Reward;
import com.example.redemptionservice.reward.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RedemptionService {
    private final RedemptionRepository redemptionRepository;
    private final RedemptionCartRepository redemptionCartRepository;
    private final RewardsAccountRepository rewardsAccountRepository;
    private final RewardRepository rewardRepository;
    private final RedemptionMapper redemptionMapper;
    private final RedemptionEventProducer redemptionEventProducer;

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

        // get reward
        var rewardIds = cart.getRedemptionCartItems().stream()
                .map(RedemptionCartItem::getRewardId)
                .toList();
        var rewardMap = rewardRepository.findAllById(rewardIds).stream()
                .collect(Collectors.toMap(Reward::getId, reward -> reward));

        // calculate total points
        var totalPoints = cart.getRedemptionCartItems().stream()
                .mapToLong(cartItem -> {
                    var reward = rewardMap.get(cartItem.getRewardId());

                    if (reward == null) {
                        throw new ResourceNotFoundException(
                                "Reward not found with id: " + cartItem.getRewardId());
                    }

                    if (!reward.getActive()) {
                        throw new BadRequestException(
                                "Reward is no longer active: " + reward.getId());
                    }

                    return reward.getPointsCost() * cartItem.getQuantity();
                }).sum();

        // get reward account
        var rewardsAccount = rewardsAccountRepository.findByCustomerId(cart.getCustomerId()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Rewards account not found for customer id: "
                                        + cart.getCustomerId()));

        // check if enough points
        if (rewardsAccount.getPointsBalance() < totalPoints) {
            throw new BadRequestException(
                    "Insufficient points to complete redemption");
        }
        Redemption redemption = new Redemption();
        redemption.setCustomerId(cart.getCustomerId());
        redemption.setTotalPoints(totalPoints);
        redemption.setStatus(RedemptionStatus.PENDING);
        var savedRedemption = redemptionRepository.save(redemption);

        // copy cart items
        for (var cartItem : cart.getRedemptionCartItems()) {
            var reward = rewardMap.get(cartItem.getRewardId());

            RedemptionItem redemptionItem = new RedemptionItem();
            redemptionItem.setRedemption(savedRedemption);
            redemptionItem.setRewardId(cartItem.getRewardId());
            redemptionItem.setQuantity(cartItem.getQuantity());
            redemptionItem.setPointsCost(reward.getPointsCost());
            savedRedemption.getRedemptionItems().add(redemptionItem);
        }

        // deduct points
        rewardsAccount.setPointsBalance(rewardsAccount.getPointsBalance() - totalPoints);

        // clear cart
        cart.clearCart();
        savedRedemption.setStatus(RedemptionStatus.COMPLETED);
        redemptionRepository.flush();

        // Kafka
        RedemptionCompletedEvent event = new RedemptionCompletedEvent();
        event.setCustomerId(cart.getCustomerId());
        event.setRedemptionId(savedRedemption.getId());
        event.setPointsChange(-totalPoints);
        event.setRemainingPointsBalance(rewardsAccount.getPointsBalance());
        event.setRewardIds(rewardIds);
        redemptionEventProducer.publishRedemptionCompleted(event);

        return buildRedemptionResponse(savedRedemption);
    }

    public RedemptionResponse getRedemptionById(Long redemptionId) {
        var redemption =  redemptionRepository.findById(redemptionId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Redemption not found with id: " + redemptionId));
        return buildRedemptionResponse(redemption);
    }

    public List<RedemptionResponse> getRedemptionsByCustomerId(Long customerId) {
        return redemptionRepository.findByCustomerId(customerId).stream()
                .map(this::buildRedemptionResponse)
                .toList();
    }

    private RedemptionResponse buildRedemptionResponse(Redemption redemption) {
        var rewardIds = redemption.getRedemptionItems().stream()
                .map(RedemptionItem::getRewardId)
                .toList();
        var rewards = rewardRepository.findAllById(rewardIds);
        var rewardMap = rewards.stream().collect(
                Collectors.toMap(Reward::getId, reward -> reward));
        var response = redemptionMapper.toRedemptionResponse(redemption);

        var items = redemption.getRedemptionItems().stream()
                .map(item -> {
                    var reward = rewardMap.get(item.getRewardId());
                    if (reward == null) {
                        throw new ResourceNotFoundException(
                                "Reward not found with id: " + item.getRewardId());
                    }
                    var itemResponse = redemptionMapper.toRedemptionItemResponse(item);
                    itemResponse.setRewardName(reward.getName());
                    return itemResponse;
                }).toList();

        response.setItems(items);
        return response;
    }
}
