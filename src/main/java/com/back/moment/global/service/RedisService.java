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

    public void setRefreshValues(String userId, String key) {
        redisTemplate.opsForHash().put("Refresh",userId,key);
        redisTemplate.expire("Refresh",Duration.ofMinutes(24 * 60L));
    }

    public void setCodeValues(String userId, String key) {
        redisTemplate.opsForHash().put("Code", userId, key);
        redisTemplate.expire("Code",Duration.ofMinutes(10L));
    }

    public String getRefreshToken(String userId){
        return (String) redisTemplate.opsForHash().get("Refresh",userId);
    }

    public String getCode(String userId){
        return (String) redisTemplate.opsForHash().get("Code",userId);
    }


    public void deleteValues(String key){
        System.out.println(key);

        redisTemplate.delete(key);
    }
}
