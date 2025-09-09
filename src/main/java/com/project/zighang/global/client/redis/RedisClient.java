package com.project.zighang.global.client.redis;

public interface RedisClient {

    void setValue(String key, String value, Long timeout);

    String getValue(String key);

    void deleteValue(String key);

    boolean checkExistsValue(String key);
}
