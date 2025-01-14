package com.videoProcessor.app.Services.Cache;

import com.videoProcessor.app.Services.Cache.Interfaces.TriFunction;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class CacheService {
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
    public <T> Page<T> getPageWithCache(String cacheValue, String identifier, Function<Pageable, Page<T>> fetcher) {
        if (identifier.contains("__")) {
            String[] identifiers = identifier.split("__");
            int page = Integer.parseInt(identifiers[0]);
            int pageSize = Integer.parseInt(identifiers[1]);
            Pageable pageable = PageRequest.of(page,pageSize);
            return fetcher.apply(pageable);
        }
        throw new IllegalArgumentException("Identifier format mismatch for TriFunction");
    }

    @Cacheable(value = "#cacheValue", key = "#identifier",unless = "#result == null")
    public <T> Page<T> getPageWithCache(String cacheValue, String identifier, BiFunction<String,Pageable, Page<T>> fetcher) {
        if (identifier.contains("__")) {
            String[] identifiers = identifier.split("__");
            int page = Integer.parseInt(identifiers[1]);
            int pageSize = Integer.parseInt(identifiers[2]);
            Pageable pageable = PageRequest.of(page,pageSize);
            return fetcher.apply(identifiers[0],pageable);
        }
        throw new IllegalArgumentException("Identifier format mismatch for TriFunction");
    }
}