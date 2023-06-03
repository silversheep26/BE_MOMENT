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

    public void setRefreshValues(String key, String userId) {
        redisTemplate.opsForHash().put("Refresh",userId,key);
        redisTemplate.expire(userId,Duration.ofMinutes(24 * 60L));
    }

    public void setCodeValues(String key, String userId) {
        redisTemplate.opsForHash().put("code", userId, key);
        redisTemplate.expire(userId,Duration.ofMinutes(10L));
    }

    public String getRefreshToken(String userId){
        return (String) redisTemplate.opsForHash().get("Refresh",userId);
    }

    public String getCode(String userId){
        return (String) redisTemplate.opsForHash().get("code",userId);
    }


    public void deleteValues(String key){
        System.out.println(key);

        redisTemplate.delete(key);
    }
}
