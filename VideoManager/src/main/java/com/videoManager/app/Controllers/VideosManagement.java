package com.videoManager.app.Controllers;


import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Config.Exceptions.UnknownRatingException;
import com.videoManager.app.Models.Records.ServerMessage;
import com.videoManager.app.Services.Kafka.KafkaSenderService;
import com.videoManager.app.Services.Videos.VideoModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
public class VideosManagement {

    private final VideoModerationService videoModerationService;

    public VideosManagement(VideoModerationService videoModerationService) {
        this.videoModerationService = videoModerationService;
    }

    @RequestMapping(value = "/registerView",method = RequestMethod.PATCH)
    public ResponseEntity<ServerMessage> registerView(@RequestParam String id){
        videoModerationService.registerView(id);
        return ResponseEntity.ok(new ServerMessage("View registered."));
    }

    @RequestMapping(value = "/rateVideo",method = RequestMethod.PATCH)
    public ResponseEntity<ServerMessage>rateVideo(@RequestParam String v, @RequestParam String rating) throws UnknownRatingException, MediaNotFoundException {
        if(rating.equalsIgnoreCase("like")){
            videoModerationService.likeVideo(v);
        } else if (rating.equalsIgnoreCase("dislike")) {
            videoModerationService.dislikeVideo(v);
        } else if (rating.equalsIgnoreCase("none")) {
            videoModerationService.removeRating(v);
        }else{
            throw new UnknownRatingException("Unknown rating '"+rating+"'. Use 'like', 'dislike' or 'none' instead");
        }
        return ResponseEntity.ok(new ServerMessage("Video has been rated."));
    }

    @RequestMapping(value = "/deleteVideo",method = RequestMethod.DELETE)
    public ResponseEntity<ServerMessage>deleteVideo(@RequestParam String videoId) throws FileServiceException, MediaNotFoundException, UnauthorizedException {
        videoModerationService.removeVideo(videoId);
        return ResponseEntity.ok(new ServerMessage("Video has been deleted."));
    }


}
