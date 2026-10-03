package com.example.rewardredemption.reward;

import com.example.rewardredemption.reward.dto.CreateRewardRequest;
import com.example.rewardredemption.reward.dto.RewardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardService rewardService;

    @GetMapping
    public List<RewardResponse> getRewards() {
        return rewardService.getAllRewards();
    }

    @GetMapping("/{rewardId}")
    public RewardResponse getReward(
            @PathVariable("rewardId") Long rewardId) {
        return rewardService.getRewardById(rewardId);
    }

    @PostMapping
    public RewardResponse createReward(
            @Valid @RequestBody CreateRewardRequest request) {
        return rewardService.createReward(request);
    }
}
