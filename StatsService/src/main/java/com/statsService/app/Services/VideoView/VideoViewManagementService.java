package com.statsService.app.Services.VideoView;

import com.statsService.app.Models.VideoView;
import com.statsService.app.Models.WatchStatus;
import com.statsService.app.Models.WatchTime;
import com.statsService.app.Repositories.VideoViewRepository;
import com.statsService.app.Services.API.ApiNinjaService;
import com.statsService.app.Services.VideoView.Interfaces.VideoViewManagementInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoViewManagementService implements VideoViewManagementInterface {

    private final VideoViewRepository videoViewRepository;

    private final ApiNinjaService apiNinjaService;

    private final RedisTemplate<String,Object> redisTemplate;

    private String getKey(String userId,String videoId){
        return "now_watching:"+userId+"__"+videoId;
    }

    @Scheduled(cron = "0 0/5 * * * ?") // 5 minutes
    public void handleWatchEndedSessions() {
        log.info("Checking for ended watching events...");
        Set<Object> endedSessionsKeys =  redisTemplate.opsForSet().members("ended_watch_sessions");
        if(endedSessionsKeys == null || endedSessionsKeys.isEmpty()){
            log.info("Events not found");
            return;
        }
        log.info("Events found. Cleaning redis memory and saving views into database.");
        List<VideoView>allViews = new ArrayList<>();
        endedSessionsKeys.stream()
                .map(keyObj -> (String)keyObj)
                .forEach(key -> {
                    try{
                        WatchTime watchTime = (WatchTime) redisTemplate.opsForValue().get(key);
                        if(watchTime != null){
                            allViews.add(watchTime.toVideoView());
                            redisTemplate.delete(key);
                            redisTemplate.opsForSet().remove("ended_watch_sessions",key);
                        }else{
                            log.error("Couldn't processed ending session with key: "+key);
                            log.error("WatchTime object is null!");
                        }

                    }catch (Exception e){
                        log.error("Couldn't processed ending session with key: "+key);
                        log.error(e.getLocalizedMessage());
                    }
                });
        if(!allViews.isEmpty()){
            try{
                videoViewRepository.saveAll(allViews);
                log.info("Saved {} views to the database",allViews.size());
            }catch (Exception e){
                log.error("Couldn't save views to the database!");
                log.error(e.getLocalizedMessage());
            }

        }
    }

    private WatchTime getWatchingTime(String key){
        if(redisTemplate.hasKey(key)){
            return (WatchTime) redisTemplate.opsForValue().get(key);
        }
        return null;
    }
    private long countAccumulatedWatchTime(long timeStarted,long accumulatedTime){
        long currentTime = System.currentTimeMillis() / 1000;
        long timeToAdd = currentTime - timeStarted;
        return accumulatedTime+timeToAdd;
    }

    @Override
    public void onPlay(String userId, String videoId) {
        String key = getKey(userId,videoId);
        WatchTime alreadyWatching = getWatchingTime(key);
        if(alreadyWatching == null){
             alreadyWatching = new WatchTime(
                    userId,
                    videoId,
                    WatchStatus.PLAYING.name(),
                    System.currentTimeMillis() / 1000,
                    0
            );
            redisTemplate.opsForValue().set(key,alreadyWatching);
            return;
        }
        if(!alreadyWatching.getStatus().equalsIgnoreCase(WatchStatus.PLAYING.name())){
            alreadyWatching.setStatus(WatchStatus.PLAYING.name());
            alreadyWatching.setTimeStarted(System.currentTimeMillis() / 1000);
            redisTemplate.opsForValue().set(key,alreadyWatching);
        }
    }

    @Override
    public void onPause(String userId, String videoId) {
        String key = getKey(userId,videoId);
        WatchTime alreadyWatching = getWatchingTime(key);
        if(alreadyWatching == null){
            log.error("Couldn't find watching event in redis memory, key: "+key);
            return;
        }
        alreadyWatching.setStatus(WatchStatus.PAUSED.name());
        alreadyWatching.setAccumulatedWatchTime(countAccumulatedWatchTime(alreadyWatching.getTimeStarted(),alreadyWatching.getAccumulatedWatchTime()));
        redisTemplate.opsForValue().set(key,alreadyWatching);
    }

    @Override
    public void onEnded(String userId, String videoId) {
        String key = getKey(userId,videoId);
        WatchTime alreadyWatching = getWatchingTime(key);
        if(alreadyWatching == null){
            log.error("Couldn't find watching event in redis memory, key: "+key);
            return;
        }
        alreadyWatching.setStatus(WatchStatus.ENDED.name());
        alreadyWatching.setAccumulatedWatchTime(countAccumulatedWatchTime(alreadyWatching.getTimeStarted(),alreadyWatching.getAccumulatedWatchTime()));
        redisTemplate.opsForValue().set(key,alreadyWatching);
        redisTemplate.opsForSet().add("ended_watch_sessions",key);
    }
}
