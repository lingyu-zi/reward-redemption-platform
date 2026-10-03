package com.example.rewardredemption.rewardsaccount;

import com.example.rewardredemption.customer.Customer;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.rewardsaccount.dto.RewardsAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardsAccountService {
    private final RewardsAccountRepository rewardsAccountRepository;
    private final RewardsAccountMapper rewardsAccountMapper;

    public void createAccount(Customer customer) {
        RewardsAccount account = new RewardsAccount();
        account.setCustomer(customer);
        account.setPointsBalance(0L);
        rewardsAccountRepository.save(account);
    }

    public RewardsAccountResponse getAccountByCustomerId(Long customerId) {
        var account = rewardsAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rewards account not found for customer " + customerId));
        return rewardsAccountMapper.toResponse(account);
    }
}
