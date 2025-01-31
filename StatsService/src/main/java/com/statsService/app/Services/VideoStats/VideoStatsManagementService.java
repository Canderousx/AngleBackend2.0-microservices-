package com.statsService.app.Services.VideoStats;

import com.statsService.app.Models.*;
import com.statsService.app.Repositories.VideoRatingRepository;
import com.statsService.app.Repositories.VideoViewRepository;
import com.statsService.app.Services.API.ApiNinjaService;
import com.statsService.app.Services.Cache.RedisService;
import com.statsService.app.Services.VideoStats.Interfaces.VideoStatsManagementInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoStatsManagementService implements VideoStatsManagementInterface {

    private final VideoViewRepository videoViewRepository;

    private final VideoRatingRepository videoRatingRepository;

    private final ApiNinjaService apiNinjaService;

    private final RedisService redisService;


    @Scheduled(cron = "0 0/5 * * * ?") // 5 minutes
    public void handleWatchEndedSessions() {
        log.info("Checking for ended watching events...");
        Set<Object> endedSessionsKeys =  redisService.getSetMembers("ended_watch_sessions");
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
                        WatchTime watchTime = redisService.get(key, WatchTime.class);
                        if(watchTime != null){
                            allViews.add(watchTime.toVideoView());
                            redisService.delete(key);
                            redisService.removeMemberFromSet("ended_watch_sessions",key);
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
    @Scheduled(cron = "0 0 */10 * * ?") // 10 hours
    public void handlePausedTooLongSessions() {
        log.info("Checking paused for too long events...");
        Set<Object> pausedSessionsKeys =  redisService.getSetMembers("paused_watch_sessions");
        if(pausedSessionsKeys == null || pausedSessionsKeys.isEmpty()){
            log.info("Found no paused events.");
            return;
        }
        log.info("Events found.");
        List<VideoView>allViews = new ArrayList<>();
        pausedSessionsKeys.stream()
                .map(keyObj -> (String)keyObj)
                .forEach(key -> {
                    try{
                        WatchTime watchTime = redisService.get(key, WatchTime.class);
                        if(watchTime != null){
                            if(System.currentTimeMillis() - watchTime.getTimePaused() >= 36000000){    // 10hrs
                                allViews.add(watchTime.toVideoView());
                                redisService.delete(key);
                                redisService.removeMemberFromSet("paused_watch_sessions",key);
                            }
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
        return redisService.get(key, WatchTime.class);
    }
    private long countAccumulatedWatchTime(long timeStarted,long accumulatedTime){
        long currentTime = System.currentTimeMillis() / 1000;
        long timeToAdd = currentTime - timeStarted;
        return accumulatedTime+timeToAdd;
    }

    @Override
    public void onPlay(String userId, String videoId) {
        String key = redisService.getWatchingKey(userId,videoId);
        WatchTime alreadyWatching = getWatchingTime(key);
        if(alreadyWatching == null){
             alreadyWatching = new WatchTime(
                    userId,
                    videoId,
                    WatchStatus.PLAYING.name(),
                    System.currentTimeMillis() / 1000,
                    0,
                     0
            );
            redisService.add(key,alreadyWatching);
            return;
        }
        if(!alreadyWatching.getStatus().equalsIgnoreCase(WatchStatus.PLAYING.name())){
            alreadyWatching.setStatus(WatchStatus.PLAYING.name());
            alreadyWatching.setTimeStarted(System.currentTimeMillis() / 1000);
            alreadyWatching.setTimePaused(0);
            redisService.add(key,alreadyWatching);
            redisService.removeMemberFromSet("paused_watch_sessions",key);
        }
    }

    @Override
    public void onPause(String userId, String videoId) {
        String key = redisService.getWatchingKey(userId,videoId);
        WatchTime alreadyWatching = getWatchingTime(key);
        if(alreadyWatching == null){
            log.error("Couldn't find watching event in redis memory, key: "+key);
            return;
        }
        alreadyWatching.setStatus(WatchStatus.PAUSED.name());
        alreadyWatching.setAccumulatedWatchTime(countAccumulatedWatchTime(alreadyWatching.getTimeStarted(),alreadyWatching.getAccumulatedWatchTime()));
        alreadyWatching.setTimePaused(System.currentTimeMillis());
        redisService.add(key,alreadyWatching);
        redisService.addMemberToSet("paused_watch_sessions",key);
    }

    @Override
    public void onEnded(String userId, String videoId) {
        String key = redisService.getWatchingKey(userId,videoId);
        WatchTime alreadyWatching = getWatchingTime(key);
        if(alreadyWatching == null){
            log.error("Couldn't find watching event in redis memory, key: "+key);
            return;
        }
        if(alreadyWatching.getStatus().equalsIgnoreCase(WatchStatus.PAUSED.name())){
            alreadyWatching.setTimePaused(0);
            redisService.removeMemberFromSet("paused_watch_sessions",key);
        }
        alreadyWatching.setStatus(WatchStatus.ENDED.name());
        alreadyWatching.setAccumulatedWatchTime(countAccumulatedWatchTime(alreadyWatching.getTimeStarted(),alreadyWatching.getAccumulatedWatchTime()));

        redisService.add(key,alreadyWatching);
        redisService.addMemberToSet("ended_watch_sessions",key);
    }

    @Scheduled(cron = "0 0/5 * * * ?") // 5 minutes
    public void saveRatingsToDatabase() {
        log.info("Checking for new ratings...");
        Set<String>keys = redisService.getKeys("video_rating:*");
        List<VideoRating> toSave = new ArrayList<>();
        if(keys != null && !keys.isEmpty()){
            log.info("Ratings found.");
            keys.forEach(key -> {
                VideoRating rating = redisService.get(key,VideoRating.class);
                if(videoRatingRepository.existsByAccountIdAndVideoId(rating.getAccountId(),rating.getVideoId())){
                    videoRatingRepository.updateRating(rating.getAccountId(),rating.getVideoId(),rating.getRating());
                }else{
                    toSave.add(rating);
                }
                redisService.delete(key);
            });
        }else{
            log.info("Ratings not found");
        }
        if(!toSave.isEmpty()){
            videoRatingRepository.saveAll(toSave);
            log.info("Ratings saved to database");
        }
    }

    private void rateVideo(String videoId, String rating){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userId == null){
            return;
        }
        String redisKey = redisService.getUnsavedRatingKey(userId,videoId);
        VideoRating videoRating = new VideoRating();
        videoRating.setVideoId(videoId);
        videoRating.setAccountId(userId);
        videoRating.setRating(rating);
        redisService.add(redisKey,videoRating);
    }

    @Override
    public void likeVideo(String videoId) {
        rateVideo(videoId,Ratings.LIKE.name());
    }

    @Override
    public void dislikeVideo(String videoId) {
        rateVideo(videoId,Ratings.DISLIKE.name());
    }

    @Override
    public void removeRating(String videoId) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(accountId == null){
            return;
        }
        String redisKey = redisService.getUnsavedRatingKey(accountId,videoId);
        if(redisService.delete(redisKey)){
            return;
        }
        redisKey = redisService.getRatingCacheKey(accountId,videoId);
        redisService.delete(redisKey);
        videoRatingRepository.deleteByAccountIdAndVideoId(accountId,videoId);
    }
}
