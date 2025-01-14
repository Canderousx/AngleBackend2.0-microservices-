package com.commentsManager.app.Services.Cache;

import com.commentsManager.app.Services.Cache.Interfaces.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class CacheService {

    private final RedisTemplate<String,Object>redisTemplate;

    private final CacheManager cacheManager;

    private final Logger logger = LogManager.getLogger(CacheService.class);

    public CacheService(RedisTemplate<String, Object> redisTemplate, CacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    public void evictCache(String value, String keyPattern){
        String pattern = value+"::"+keyPattern;
        Set<String>keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            logger.info("Clearing cache... pattern: "+pattern);
            redisTemplate.delete(keys);
        }
    }

    public void evictAll(String value){
        cacheManager.getCache(value).clear();
    }


    @Cacheable(value = "#cacheValue", key = "#identifier",unless = "#result == null")
    public <T> T getWithCache(String cacheValue,String identifier, Function<String, T> fetcher) {
        return fetcher.apply(identifier);
    }


    @Cacheable(value = "#cacheValue", key = "#identifier",unless = "#result == null")
    public <T> T getWithCache(String cacheValue, String identifier, BiFunction<String, String, T> fetcher) {
        if (identifier.contains("__")) {
            String[] identifiers = identifier.split("__");
            return fetcher.apply(identifiers[0], identifiers[1]);
        }
        throw new IllegalArgumentException("Identifier format mismatch for BiFunction");
    }

    @Cacheable(value = "#cacheValue", key = "#identifier",unless = "#result == null")
    public <T> T getWithCache(String cacheValue, String identifier, TriFunction<String, String,String, T> fetcher) {
        if (identifier.contains("__")) {
            String[] identifiers = identifier.split("__");
            return fetcher.apply(identifiers[0], identifiers[1],identifiers[2]);
        }
        throw new IllegalArgumentException("Identifier format mismatch for TriFunction");
    }

    @Cacheable(value = "#cacheValue", key = "#identifier",unless = "#result == null")
    public <T> PageWrapper<T> getPageWithCache(String cacheValue, String identifier, Function<Pageable, Page<T>> fetcher) {
        if (identifier.contains("__")) {
            String[] identifiers = identifier.split("__");
            int page = Integer.parseInt(identifiers[0]);
            int pageSize = Integer.parseInt(identifiers[1]);
            Pageable pageable = PageRequest.of(page,pageSize);
            Page<T>result = fetcher.apply(pageable);
            return new PageWrapper<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements());
        }
        throw new IllegalArgumentException("Identifier format mismatch");
    }

    @Cacheable(value = "#cacheValue", key = "#identifier",unless = "#result == null")
    public <T> PageWrapper<T> getPageWithCache(String cacheValue, String identifier, BiFunction<String,Pageable, Page<T>> fetcher) {
        if (identifier.contains("__")) {
            String[] identifiers = identifier.split("__");
            int page = Integer.parseInt(identifiers[1]);
            int pageSize = Integer.parseInt(identifiers[2]);
            Pageable pageable = PageRequest.of(page,pageSize);
            Page<T> result = fetcher.apply(identifiers[0],pageable);
            return new PageWrapper<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements());
        }
        throw new IllegalArgumentException("Identifier format mismatch for TriFunction");
    }

    public void evictFromCache(String value, String identifier){
        if(cacheManager.getCache(value)!= null){
            cacheManager.getCache(value).evict(identifier);
        }
    };
}