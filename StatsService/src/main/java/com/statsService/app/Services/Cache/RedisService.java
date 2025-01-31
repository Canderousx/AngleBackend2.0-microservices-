package com.statsService.app.Services.Cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String,Object>redisTemplate;

    public String getUnsavedRatingKey(String userId, String videoId){return "video_rating:"+userId+"__"+videoId;}

    public String getRatingCacheKey(String userId, String videoId){return "rating_cache:"+userId+"__"+videoId;}

    public String getWatchingKey(String userId, String videoId){
        return "now_watching:"+userId+"__"+videoId;
    }

    public String getLikesCacheKey(String videoId){return "likes:"+videoId;}

    public String getViewsCacheKey(String videoId){return "views:"+videoId;}

    public String getDislikesCacheKey(String videoId){return "dislikes:"+videoId;}


    public boolean keyExists(String key){
        return redisTemplate.hasKey(key);
    }

    public Set<String>getKeys(String pattern){
        return redisTemplate.keys(pattern);
    }

    public Set<Object> getSetMembers(String key){
        return redisTemplate.opsForSet().members(key);
    }

    public void addMemberToSet(String key,String member){
        redisTemplate.opsForSet().add(key,member);
    }
    public void removeMemberFromSet(String key,String member){
        redisTemplate.opsForSet().remove(key,member);
    }

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
