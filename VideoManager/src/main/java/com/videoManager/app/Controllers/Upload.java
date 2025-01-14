package com.videoManager.app.Controllers;


import com.videoManager.app.Config.Exceptions.FileStoreException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.ThumbnailsNotReadyYetException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Models.Records.ServerMessage;
import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Services.Videos.VideoModerationService;
import com.videoManager.app.Services.Videos.VideoUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload")
public class Upload {

    private final VideoUploadService videoUploadService;

    private final VideoModerationService videoModerationService;



    public Upload(VideoUploadService videoUploadService, VideoModerationService videoModerationService) {
        this.videoUploadService = videoUploadService;
        this.videoModerationService = videoModerationService;
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage> uploadVideo(@RequestParam("file") MultipartFile file) throws FileStoreException {
        this.videoUploadService.uploadVideo(file);
        return ResponseEntity.ok(new ServerMessage("Video has been uploaded and is now being processed"));
    }

    @RequestMapping(value = "/getThumbnails",method = RequestMethod.GET)
    public List<String>getThumbnails(@RequestParam String v) throws ThumbnailsNotReadyYetException {
        return videoUploadService.getThumbnails(v);
    }

    @RequestMapping(value = "/setMetadata",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>setMetadata(@RequestParam String id, @RequestBody VideoDetails video) throws MediaNotFoundException, UnauthorizedException {
        videoModerationService.setMetadata(id,video);
        return ResponseEntity.ok(new ServerMessage("Metadata has been updated"));
    }
}
