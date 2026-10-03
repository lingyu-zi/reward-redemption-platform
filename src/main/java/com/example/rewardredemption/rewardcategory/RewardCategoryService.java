package com.example.rewardredemption.rewardcategory;

import com.example.rewardredemption.exception.DuplicateResourceException;
import com.example.rewardredemption.exception.ResourceNotFoundException;
import com.example.rewardredemption.rewardcategory.dto.CreateRewardCategoryRequest;
import com.example.rewardredemption.rewardcategory.dto.RewardCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardCategoryService {
    private final RewardCategoryRepository rewardCategoryRepository;
    private final RewardCategoryMapper rewardCategoryMapper;

    public List<RewardCategoryResponse> getAllCategories() {
        return rewardCategoryRepository.findAll().stream()
                .map(rewardCategoryMapper::toResponse)
                .toList();
    }

    public RewardCategoryResponse getCategoryById(Long categoryId) {
        var rewardCategory = rewardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with categoryId " + categoryId));
        return rewardCategoryMapper.toResponse(rewardCategory);
    }

    @Transactional
    public RewardCategoryResponse createCategory(
            CreateRewardCategoryRequest request){
        if (rewardCategoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists");
        }
        RewardCategory rewardCategory = rewardCategoryMapper.toEntity(request);
        var savedCategory = rewardCategoryRepository.save(rewardCategory);
        return rewardCategoryMapper.toResponse(savedCategory);
    }
}
