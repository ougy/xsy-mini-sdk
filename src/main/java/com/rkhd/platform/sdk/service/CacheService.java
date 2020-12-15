package com.rkhd.platform.sdk.service;

import com.rkhd.platform.sdk.exception.CacheException;

import java.util.HashMap;
import java.util.Map;

public class CacheService {
    private static Integer MAX_CACHE_KEY_LENGTH = Integer.valueOf(64);
    private static Integer MAX_CACHE_VALUE_LENGTH = Integer.valueOf(204800);
    private static Map<String, String> cache = new HashMap<>();

    private static CacheService singleton = new CacheService();

    public static CacheService instance() {
        return singleton;
    }

    public String get(String key) throws CacheException {
        checkCacheKey(key);
        return cache.get(key);
    }

    public Boolean set(String key, String value) throws CacheException {
        checkCacheKey(key);
        checkCacheValue(value);
        cache.put(key, value);
        return Boolean.valueOf(true);
    }

    public Boolean setIfNotChange(String key, String value) throws CacheException {
        checkCacheKey(key);
        checkCacheValue(value);
        cache.put(key, value);
        return Boolean.valueOf(true);
    }

    public Boolean delete(String key) throws CacheException {
        checkCacheKey(key);
        cache.remove(key);
        return Boolean.valueOf(true);
    }

    private void checkCacheKey(String key) throws CacheException {
        if (key.length() > MAX_CACHE_KEY_LENGTH.intValue()) {
            String errorMsg = "调用cache服务时，缓存的key长度超限，限制长度为：" + MAX_CACHE_KEY_LENGTH;
            throw new CacheException(errorMsg);
        }
    }

    private void checkCacheValue(String value) throws CacheException {
        if (value.length() > MAX_CACHE_VALUE_LENGTH.intValue()) {
            String errorMsg = "调用cache服务时，缓存的value长度超限，限制长度为：" + MAX_CACHE_VALUE_LENGTH;
            throw new CacheException(errorMsg);
        }
    }
}