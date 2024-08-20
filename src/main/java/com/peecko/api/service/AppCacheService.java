package com.peecko.api.service;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppCacheService {
    final CacheManager cacheManager;

    public AppCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "AppCache", key = "#key")
    public String getData(String key) {
        // Simulate a time-consuming operation, like a database call
        return "Data for key: " + key;
    }

    @CacheEvict(value = "AppCache", key = "#key")
    public void evictCache(String key) {
        // This method will remove the cache entry for the given key
    }

    @CacheEvict(value = "AppCache", allEntries = true)
    public void evictAllCache() {
        // This method will clear all cache entries
    }
}
