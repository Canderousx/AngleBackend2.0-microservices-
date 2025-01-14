package com.thumbnailGenerator.app.Controllers;

import com.thumbnailGenerator.app.Models.Records.VideoProcessingData;
import com.thumbnailGenerator.app.Services.FFMpeg.FFMpegDataRetrievalService;
import com.thumbnailGenerator.app.Services.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final FFMpegDataRetrievalService ffmpegService;

    private final Logger logger = LogManager.getLogger(KafkaListeners.class);

    public KafkaListeners(FFMpegDataRetrievalService ffmpegService) {
        this.ffmpegService = ffmpegService;
    }


    @KafkaListener(topics = "video_uploaded", groupId = "thumbnails_group")
    public void videoUploaded(String json){
        logger.info("Video uploaded event received.");
        VideoProcessingData data = JsonUtils.readJson(json, VideoProcessingData.class);
        logger.info("====RECEIVED VIDEO DATA====");
        logger.info("ID: "+data.videoId());
        logger.info("RAW FILE: "+data.rawFilePath());
        logger.info("************************************");
        logger.info("engaging generator...");
        ffmpegService.generateVideoThumbnails(
                data.videoId(),
                data.rawFilePath()
        );
    }

}
