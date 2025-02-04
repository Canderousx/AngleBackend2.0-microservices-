package com.commentsManager.app.Services.Cache;

import com.commentsManager.app.Services.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCache {
    private final Duration timeValid = Duration.ofSeconds(10);

    private final RedisTemplate<String,String>redisTemplate;
    public String getVideoCommentsKey(String videoId,int page, int pageSize){return "comments_"+page+"_"+pageSize+":"+videoId;}

    public String getCommentsNumberKey(String videoId){return "comments_num:"+videoId;}

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


}
