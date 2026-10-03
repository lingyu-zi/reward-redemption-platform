package com.example.rewardredemption.rewardcategory;

import com.example.rewardredemption.rewardcategory.dto.CreateRewardCategoryRequest;
import com.example.rewardredemption.rewardcategory.dto.RewardCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reward-categories")
public class RewardCategoryController {
    private final RewardCategoryService rewardCategoryService;

    @GetMapping
    public List<RewardCategoryResponse> getAllCategories() {
        return rewardCategoryService.getAllCategories();
    }

    @GetMapping("/{categoryId}")
    public RewardCategoryResponse getCategoryById(@PathVariable Long categoryId) {
        return rewardCategoryService.getCategoryById(categoryId);
    }

    @PostMapping
    public RewardCategoryResponse createCategory(
            @Valid @RequestBody CreateRewardCategoryRequest request){
        return rewardCategoryService.createCategory(request);
    }
}
