package com.videoManager.app.Controllers;


import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Config.Exceptions.UnknownRatingException;
import com.videoManager.app.Models.Records.ServerMessage;
import com.videoManager.app.Services.Kafka.KafkaSenderService;
import com.videoManager.app.Services.Videos.VideoModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
public class VideosManagement {

    private final VideoModerationService videoModerationService;

    @RequestMapping(value = "/deleteVideo",method = RequestMethod.DELETE)
    public ResponseEntity<ServerMessage>deleteVideo(@RequestParam String videoId) throws FileServiceException, MediaNotFoundException, UnauthorizedException {
        videoModerationService.removeVideo(videoId);
        return ResponseEntity.ok(new ServerMessage("Video has been deleted."));
    }


}
