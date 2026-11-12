package com.example.fulfillmentservice.merchant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MerchantProviderConfig {

    @Bean
    public RestClient merchantProviderRestClient(
            @Value("${merchant-provider.base-url}") String baseUrl) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
