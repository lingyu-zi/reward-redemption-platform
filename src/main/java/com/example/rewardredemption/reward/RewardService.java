package com.example.rewardredemption.reward;

import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.merchant.MerchantRepository;
import com.example.rewardredemption.reward.dto.CreateRewardRequest;
import com.example.rewardredemption.reward.dto.RewardResponse;
import com.example.rewardredemption.rewardcategory.RewardCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {
    private final RewardRepository rewardRepository;
    private final RewardCategoryRepository rewardCategoryRepository;
    private final MerchantRepository merchantRepository;
    private final RewardMapper rewardMapper;

    public List<RewardResponse> getAllRewards() {
         return rewardRepository.findAll().stream()
                 .map(rewardMapper::toResponse)
                 .toList();
    }

    public RewardResponse getRewardById(Long id) {
        var reward = rewardRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Reward not found with id: " + id));
        return rewardMapper.toResponse(reward);
    }

    @Transactional
    public RewardResponse createReward(CreateRewardRequest request){
        var category = rewardCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        var merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Merchant not found with id: " + request.getMerchantId()
                ));
        var reward = rewardMapper.toEntity(request);
        reward.setCategory(category);
        reward.setMerchant(merchant);
        reward.setActive(true);
        var savedReward = rewardRepository.save(reward);
        return rewardMapper.toResponse(savedReward);
    }
}
