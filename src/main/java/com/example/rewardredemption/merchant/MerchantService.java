package com.example.rewardredemption.merchant;

import com.example.rewardredemption.exception.DuplicateResourceException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.merchant.dto.CreateMerchantRequest;
import com.example.rewardredemption.merchant.dto.MerchantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    public List<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll().stream()
                .map(merchantMapper::toResponse)
                .toList();
    }

    public MerchantResponse getMerchantById(Long merchantId) {
        var merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Merchant not found with id " + merchantId));
        return merchantMapper.toResponse(merchant);
    }

    @Transactional
    public MerchantResponse createMerchant(
            CreateMerchantRequest request) {
        if (merchantRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Merchant already exists with name: " + request.getName());
        }
        var merchant = merchantMapper.toEntity(request);
        merchant.setActive(true);
        var savedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(savedMerchant);
    }
}
