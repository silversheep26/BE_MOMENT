package com.back.moment.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setValues(String key, String userId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, userId, Duration.ofDays(2L));
    }

    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteValues(String key){
        System.out.println(key);

        redisTemplate.delete(key);
    }
}
