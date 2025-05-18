package com.example.recipes.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    private static final int MAX_CACHE_SIZE = 100;

    private final Map<String, Object> cache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            boolean shouldRemove = size() > MAX_CACHE_SIZE;
            if (shouldRemove) {
                logger.info("Removing eldest cache entry: {}", eldest.getKey());
            }
            return shouldRemove;
        }
    };

    public Object get(String key) {
        logger.info("Getting data from cache for key: {}", key);
        return cache.get(key);
    }

    public void put(String key, Object value) {
        logger.info("Putting data into cache for key: {}", key);
        cache.put(key, value);
    }

    public void clear() {
        logger.info("Clearing the cache");
        cache.clear();
    }

    public void evict(String key) {
        if (cache.containsKey(key)) {
            logger.info("Evicting cache entry with key: {}", key);
            cache.remove(key);
        } else {
            logger.warn("Cache entry with key {} not found", key);
        }
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public void evictByPattern(String pattern) {
         //String normalizedPattern = pattern.replace("*", "");
        Set<String> keysToRemove = cache.keySet().stream()
                .filter(key -> key.startsWith(pattern.replace("*", "")))
                .collect(Collectors.toSet());
        keysToRemove.forEach(this::evict);
        logger.info("Evicted {} entries by pattern: {}", keysToRemove.size(), pattern);
    }

    public Set<String> getCachedKeys() {
        logger.info("Current cache keys: {}", cache.keySet());
        return cache.keySet();
    }
}
