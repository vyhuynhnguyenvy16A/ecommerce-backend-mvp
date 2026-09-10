package com.example.demo.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bandwidth loginRateLimit() {
        return Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
    }
}
