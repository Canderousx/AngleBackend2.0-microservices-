package com.statsService.app.Services.VideoStats;

import com.statsService.app.Models.*;
import com.statsService.app.Repositories.VideoRatingRepository;
import com.statsService.app.Repositories.VideoViewRepository;
import com.statsService.app.Services.API.ApiNinjaService;
import com.statsService.app.Services.Cache.RedisService;
import com.statsService.app.Services.JsonUtils;
import com.statsService.app.Services.Kafka.KafkaSenderService;
import com.statsService.app.Services.VideoStats.Interfaces.VideoStatsManagementInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoStatsManagementService implements VideoStatsManagementInterface {

    private final VideoViewRepository videoViewRepository;

    private final VideoRatingRepository videoRatingRepository;

    private final KafkaSenderService kafkaSenderService;

    private final ApiNinjaService apiNinjaService;

    private final RedisService redisService;


    /*
     * Sends a Kafka event with updated video views count.
     * updateData map contains videoIds as keys and the numbers of their new views as values
     */
    private void viewsUpdateEvent(Map<String,Long>updateData){
        String data = JsonUtils.toJson(updateData);
        kafkaSenderService.send("views_update",data);
    }


    /*
    Triggers when user closes the connection.
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if(principal == null){
            return;
        }
        String userId = accessor.getUser().getName();
        log.info("{} disconnected.",userId);
        handleUserDisconnectEvent(userId);

    }

    /*
    Handles user disconnection event.
    Retrieves from redis all watch session keys matching the pattern "now_watching:{userId}__*" and for each key found calls
    OnEnded method to finalize watch session.
    It allows to make sure no active watching sessions remain in a memory.
     */
    private void handleUserDisconnectEvent(String userId){
        String keyPattern = "now_watching:"+userId+"__*";
        Set<String>redisKeys = redisService.getKeys(keyPattern);
        if(!redisKeys.isEmpty()){
            log.info("Found {} watching events of a user {}",redisKeys.size(),userId);
            redisKeys.forEach(this::onEnded);
        }
    }


    /*
    Runs every 5 minutes
    Processes ended watch sessions stored in Redis as a set of watchTime keys saved under the "ended_watch_sessions" key.
    When ended watch session is found, it is converted to VideoView entity and added to ArrayList and removed from Redis.
    At the end if the list is not empty, all its entities are saved to the database and the Kafka event is being sent with the updated view counts.
     */
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
        Map<String,Long>updateData = new HashMap<>();
        endedSessionsKeys.stream()
                .map(keyObj -> (String)keyObj)
                .forEach(key -> {
                    try{
                        WatchTime watchTime = redisService.get(key, WatchTime.class);
                        if(watchTime != null){
                            String videoId = watchTime.getVideoId();
                            allViews.add(watchTime.toVideoView());
                            redisService.delete(key);
                            redisService.removeMemberFromSet("ended_watch_sessions",key);
                            updateData.merge(videoId,1L,Long::sum);

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
                if(!updateData.isEmpty()){
                    viewsUpdateEvent(updateData);
                }
            }catch (Exception e){
                log.error("Couldn't save views to the database!");
                log.error(e.getLocalizedMessage());
            }

        }
    }

    /*
    Runs every 10 hours.
    Handles video watch sessions that have been paused for too long. Every paused session key is stored in Redis in a Set of watchTime keys under "paused_watch_sessions" key.
    If a session has been paused for at least 10 hours it is treated like ended watch session
     */
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
        Map<String,Long>updateData = new HashMap<>();
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
                                updateData.merge(watchTime.getVideoId(), 1L,Long::sum);
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
                if(!updateData.isEmpty()){
                    viewsUpdateEvent(updateData);
                }
            }catch (Exception e){
                log.error("Couldn't save views to the database!");
                log.error(e.getLocalizedMessage());
            }

        }
    }


    /*
     Retrieves the watch time data from Redis with a given key.
     returns the WatchTime object if found or null.
     */
    private WatchTime getWatchingTime(String key){
        return redisService.get(key, WatchTime.class);
    }



    /*
     Calculates the total accumulated watch time by adding the current watch session duration.
     timeStarted - timestamp in seconds of a moment when user clicked 'play the video'
     accumulatedTime - previous accumulated watch time
     */
    private long countAccumulatedWatchTime(long timeStarted,long accumulatedTime){
        long currentTime = System.currentTimeMillis() / 1000;
        long timeToAdd = currentTime - timeStarted;
        return accumulatedTime+timeToAdd;
    }



    /*
    Handles the start of a video watching session.
    Checks if a session already exists:
            - if not, creates a new watch session in Redis
            - if the session was paused, resumes the playback and resets pause time.

    userId is the id of the user watching the video - if it's not logged-in user, the id is "ANONYMOUS_{ip_address}"
    videoId is the id of the video that is currently being watched.
     */
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
            if(alreadyWatching.getStatus().equalsIgnoreCase(WatchStatus.ENDED.name())){
                log.info("Session previously marked as ended. Removing from ended_sessions keys set");
                redisService.removeMemberFromSet("ended_watch_sessions",key);
            }
            alreadyWatching.setStatus(WatchStatus.PLAYING.name());
            alreadyWatching.setTimeStarted(System.currentTimeMillis() / 1000);
            alreadyWatching.setTimePaused(0);
            redisService.add(key,alreadyWatching);
            redisService.removeMemberFromSet("paused_watch_sessions",key);
        }
    }


    /*
    Handles the pause event of a video watching session.
    Checks if a session already exists:
            - if not it stops working and the error is logged. At this point the session should exist.
    Updates the watch session status to 'PAUSED'
    Calculates and stores accumulated watch time.
    Adds the key of the session to the "paused_watch_sessions" Redis set
    Saves the timestamp of the event.

    userId is the id of the user watching the video - if it's not logged-in user, the id is "ANONYMOUS_{ip_address}"
    videoId is the id of the video that is currently being watched.
     */
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

    @Override
    public void onEnded(String key) {
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
