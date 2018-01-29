package com.rackspacecloud.blueflood.elasticsearchqueryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AutoConfig {
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
