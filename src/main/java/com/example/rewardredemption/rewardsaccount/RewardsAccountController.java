package com.example.rewardredemption.rewardsaccount;

import com.example.rewardredemption.rewardsaccount.dto.RewardsAccountResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rewards-accounts")
public class RewardsAccountController {
    private final RewardsAccountService rewardsAccountService;

    @GetMapping("/customer/{customerId}")
    public RewardsAccountResponse getAccountByCustomerId(
            @PathVariable Long customerId){
        return rewardsAccountService.getAccountByCustomerId(customerId);
    }
}
