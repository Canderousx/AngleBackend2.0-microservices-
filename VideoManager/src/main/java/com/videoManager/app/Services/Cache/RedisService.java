package com.videoManager.app.Services.Cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String,Object> redisTemplate;

    public String getViewsCacheKey(String videoId){return "views:"+videoId;}

    public String getStreamUrlCacheKey(String videoId){return "stream_url:"+videoId;}

    public boolean delete(String key){
        return redisTemplate.delete(key);
    }

    public void add(String key, Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    public void add(String key, Object value, Duration duration){
        redisTemplate.opsForValue().set(key,value,duration);
    }

    public <T> T get(String key, Class<T> clazz){
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return null;
        }
        if(!clazz.isInstance(value)){
            throw new ClassCastException("Expected type: "+clazz.getName()+". Found: "+value.getClass().getName());
        }
        return clazz.cast(value);
    }



}
