package com.statsService.app.Controllers;

import com.statsService.app.Models.Records.WatchTimeRecord;
import com.statsService.app.Services.VideoView.VideoViewManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/watchTime")
@RequiredArgsConstructor
public class WatchTime {

    private final VideoViewManagementService videoViewManagementService;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ResponseEntity<String>registerView(@RequestBody WatchTimeRecord watchTime, HttpServletRequest request){
        videoViewManagementService.registerView(watchTime,request.getRemoteAddr());
        return ResponseEntity.ok("");
    }


}
