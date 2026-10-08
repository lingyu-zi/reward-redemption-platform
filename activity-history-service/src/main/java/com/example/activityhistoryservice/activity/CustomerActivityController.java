package com.example.activityhistoryservice.activity;

import com.example.activityhistoryservice.activity.dto.CustomerActivityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/activities")
public class CustomerActivityController {
    private final CustomerActivityService customerActivityService;

    @GetMapping("/customer/{customerId}")
    public List<CustomerActivityResponse> getCustomerActivities(
            @PathVariable Long customerId){
        return customerActivityService.getCustomerActivities(customerId);
    }
}
