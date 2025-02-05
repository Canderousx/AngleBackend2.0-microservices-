package com.videoManager.app.Services.Cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Services.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoCache {

    // default lifetime of a cached data.
    private final Duration timeValid = Duration.ofMinutes(5);

    private final RedisTemplate<String,String>redisTemplate;

    // key of a single video cache
    public String getVideoKey(String videoId){return "video:"+videoId;}

    // key of a list of videos cache
    public String getVideoListKey(){return "list:";}

    // key of a page of videos cache
    public String getVideoPageKey(int page, int pageSize){return "page_"+page+"_size_"+pageSize+":";}

    public <T> void saveToCache(String key, T value){
        String json = JsonUtils.toJson(value);
        redisTemplate.opsForValue().set(key,json,timeValid);
        log.info("Saved cache key {}",key);
    }
    public <T> void saveToCache(String key, T value,Duration duration){
        String json = JsonUtils.toJson(value);
        redisTemplate.opsForValue().set(key,json,duration);
        log.info("Saved cache key {}",key);
    }

    public void removeFromCache(String key){
        redisTemplate.delete(key);
    }



    public <T> T getFromCache(String key, Class<T> clazz){
        String json = redisTemplate.opsForValue().get(key);
        if(json == null){
            return null;
        }
        return JsonUtils.readJson(json,clazz);
    }

    public <T> T getFromCache(String key, TypeReference<T> reference){
        String json = redisTemplate.opsForValue().get(key);
        if(json == null){
            return null;
        }
        return JsonUtils.readJson(json,reference);
    }

    /*
    Methods below are meant to reduce boilerplate code in retrieval services.
    They can get any data stored in redis and allow us to inject a code that will download the data from database if there is no data stored in the cache and then save it.
     */

    public <T> T getFromCacheOrFetch(String cacheKey, TypeReference<T> typeReference, Supplier<T> fetchFunction, Duration cacheDuration) {
        T cachedData = getFromCache(cacheKey, typeReference);
        if (cachedData != null) {
            return cachedData;
        }
        T fetchedData = fetchFunction.get();
        if (fetchedData != null) {
            saveToCache(cacheKey, fetchedData, cacheDuration);
        }
        return fetchedData;
    }

    public  <T> T getFromCacheOrFetch(String cacheKey, Class<T> clazz, Supplier<T> fetchFunction, Duration cacheDuration) {
        T cachedData = getFromCache(cacheKey, clazz);
        if (cachedData != null) {
            return cachedData;
        }
        T fetchedData = fetchFunction.get();
        if (fetchedData != null) {
            saveToCache(cacheKey, fetchedData, cacheDuration);
        }
        return fetchedData;
    }

    public <T> T getFromCacheOrFetch(String cacheKey, TypeReference<T> typeReference, Supplier<T> fetchFunction) {
        T cachedData = getFromCache(cacheKey, typeReference);
        if (cachedData != null) {
            return cachedData;
        }
        T fetchedData = fetchFunction.get();
        if (fetchedData != null) {
            saveToCache(cacheKey, fetchedData, timeValid);
        }
        return fetchedData;
    }

    public  <T> T getFromCacheOrFetch(String cacheKey, Class<T> clazz, Supplier<T> fetchFunction) {
        T cachedData = getFromCache(cacheKey, clazz);
        if (cachedData != null) {
            return cachedData;
        }
        T fetchedData = fetchFunction.get();
        if (fetchedData != null) {
            saveToCache(cacheKey, fetchedData, timeValid);
        }
        return fetchedData;
    }

}
