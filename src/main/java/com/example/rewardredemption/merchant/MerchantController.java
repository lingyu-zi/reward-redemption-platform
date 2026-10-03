package com.example.rewardredemption.merchant;

import com.example.rewardredemption.merchant.dto.CreateMerchantRequest;
import com.example.rewardredemption.merchant.dto.MerchantResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping
    public List<MerchantResponse> getMerchants() {
        return merchantService.getAllMerchants();
    }

    @GetMapping("/{id}")
    public MerchantResponse getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }

    @PostMapping
    public MerchantResponse createMerchant(
            @Valid @RequestBody CreateMerchantRequest request){
        return merchantService.createMerchant(request);
    }
}
