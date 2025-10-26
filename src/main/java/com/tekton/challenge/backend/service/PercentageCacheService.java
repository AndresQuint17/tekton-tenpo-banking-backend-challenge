package com.tekton.challenge.backend.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PercentageCacheService {

    private final CacheManager cacheManager;

    public PercentageCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void savePercentage(double value) {
        Cache cache = cacheManager.getCache("percentageCache");
        if (cache != null) {
            cache.put("latest", value); // Esto actualiza el valor y reinicia el tiempo de vida del cache
        }
    }

    public Optional<Double> getCachedPercentage() {
        Cache cache = cacheManager.getCache("percentageCache");
        if (cache != null) {
            Double value = cache.get("latest", Double.class);
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    @Scheduled(fixedRate = 1800000) // 30 min
    public void clearCache() {
        Cache cache = cacheManager.getCache("percentageCache");
        if (cache != null) cache.clear();
    }

}
