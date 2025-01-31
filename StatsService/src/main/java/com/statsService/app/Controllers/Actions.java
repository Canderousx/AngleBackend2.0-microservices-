package com.statsService.app.Controllers;


import com.statsService.app.Config.Exceptions.UnknownRatingException;
import com.statsService.app.Models.Records.ServerMessage;
import com.statsService.app.Services.VideoStats.VideoStatsManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actions")
@RequiredArgsConstructor
public class Actions {

    private final VideoStatsManagementService videoStatsManagementService;

    @RequestMapping(value = "/rateVideo",method = RequestMethod.PATCH)
    public ResponseEntity<ServerMessage> rateVideo(@RequestParam String v, @RequestParam String rating) throws UnknownRatingException{
        if(rating.equalsIgnoreCase("like")){
            videoStatsManagementService.likeVideo(v);
        } else if (rating.equalsIgnoreCase("dislike")) {
            videoStatsManagementService.dislikeVideo(v);
        } else if (rating.equalsIgnoreCase("none")) {
            videoStatsManagementService.removeRating(v);
        }else{
            throw new UnknownRatingException("Unknown rating '"+rating+"'. Use 'like', 'dislike' or 'none' instead");
        }
        return ResponseEntity.ok(new ServerMessage("Video has been rated."));
    }
}
