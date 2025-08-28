package com.project.zighang.global.redis;

import com.project.zighang.global.exception.ApiResponseCode;
import com.project.zighang.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import com.project.zighang.global.exception.Error;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisClient {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, String value, Long timeout) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, value, Duration.ofMillis(timeout));
        } catch (Exception e) {
            throw new BadRequestException(Error.REDIS_SET_ERROR, Error.REDIS_SET_ERROR.getMessage());
        }
    }

    public String getValue(String key) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            if (values.get(key) == null) {
                return "";
            }
            return values.get(key).toString();
        } catch (Exception e) {
            throw new BadRequestException(Error.REDIS_GET_ERROR, Error.REDIS_GET_ERROR.getMessage());
        }
    }

    public void deleteValue(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new BadRequestException(Error.REDIS_DELETE_ERROR, Error.REDIS_DELETE_ERROR.getMessage());
        }
    }

    public boolean checkExistsValue(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            throw new BadRequestException(Error.SHA256_GENERATION_ERROR, Error.SHA256_GENERATION_ERROR.getMessage());
        }
    }
}
