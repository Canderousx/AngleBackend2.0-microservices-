package com.authService.app.Services.Cache;
import com.authService.app.Services.Cache.Interfaces.TriFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;
    public void evictFromCache(String key){
        cacheManager.getCache("auth_cache").evict(key);
    }

    @Cacheable(value = "auth_cache", key = "#identifier",unless = "#result == null")
    public <T> T getWithCache(String identifier, Function<String, T> fetcher) {
        String arg = identifier.split("__")[0];
        return fetcher.apply(arg);
    }


    @Cacheable(value = "auth_cache", key = "#identifier",unless = "#result == null")
    public <T> T getWithCache(String identifier, BiFunction<String, String, T> fetcher) {
        if (identifier.contains("__")) {
            String[] args = identifier.split("__");
            return fetcher.apply(args[0], args[1]);
        }
        throw new IllegalArgumentException("Identifier format mismatch for BiFunction");
    }

    @Cacheable(value = "auth_cache", key = "#identifier",unless = "#result == null")
    public <T> T getWithCache(String identifier, TriFunction<String, String,String, T> fetcher) {
        if (identifier.contains("__")) {
            String[] args = identifier.split("__");
            return fetcher.apply(args[0], args[1],args[2]);
        }
        throw new IllegalArgumentException("Identifier format mismatch for TriFunction");
    }

    @Cacheable(value = "auth_cache", key = "#identifier",unless = "#result == null")
    public <T> PageWrapper<T> getPageWithCache(String identifier, Function<Pageable, Page<T>> fetcher) {
        if (identifier.contains("__")) {
            String[] args = identifier.split("__");
            int page = Integer.parseInt(args[0]);
            int pageSize = Integer.parseInt(args[1]);
            Pageable pageable = PageRequest.of(page,pageSize);
            Page<T>result = fetcher.apply(pageable);
            return new PageWrapper<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements());
        }
        throw new IllegalArgumentException("Identifier format mismatch");
    }

    @Cacheable(value = "auth_cache", key = "#identifier",unless = "#result == null")
    public <T> PageWrapper<T> getPageWithCache(String identifier, BiFunction<String,Pageable, Page<T>> fetcher) {
        if (identifier.contains("__")) {
            String[] args = identifier.split("__");
            int page = Integer.parseInt(args[1]);
            int pageSize = Integer.parseInt(args[2]);
            Pageable pageable = PageRequest.of(page,pageSize);
            Page<T> result = fetcher.apply(args[0],pageable);
            return new PageWrapper<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements());
        }
        throw new IllegalArgumentException("Identifier format mismatch for TriFunction");
    }

}
