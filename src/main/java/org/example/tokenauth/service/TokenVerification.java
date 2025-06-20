package org.example.tokenauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.example.tokenauth.config.RedisConfig;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenVerification {

    private final RedisTemplate<String, Long> redisTemplate;
    private final long tokenTtl;

    public TokenVerification(RedisTemplate<String, Long> redisTemplate,
                             @Value("${app.auth.token-ttl}") long tokenTtl) {
        this.redisTemplate = redisTemplate;
        this.tokenTtl = tokenTtl;
    }

    public boolean verify(UUID token) {
        String tokenKey = "token:" + token.toString();
        Long expirationTime = redisTemplate.opsForValue().get(tokenKey);

        if (expirationTime == null) {
            return false;
        }

        return expirationTime > System.currentTimeMillis();
    }

    public UUID generateToken() {
        UUID token = UUID.randomUUID();
        String tokenKey = "token:" + token.toString();
        long expirationTime = System.currentTimeMillis() + (tokenTtl * 1000);

        redisTemplate.opsForValue().set(tokenKey, expirationTime, tokenTtl, TimeUnit.SECONDS);
        return token;
    }
}
