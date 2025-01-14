package com.videoProcessor.app.Controllers;

import com.videoProcessor.app.Models.Records.VideoProcessingData;
import com.videoProcessor.app.Services.FFMpeg.FFMpegConverterService;
import com.videoProcessor.app.Services.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final Logger logger = LogManager.getLogger(KafkaListeners.class);

    private final FFMpegConverterService converterService;

    public KafkaListeners(FFMpegConverterService converterService) {
        this.converterService = converterService;
    }

    @KafkaListener(topics = "video_uploaded", groupId = "video_group")
    public void videoUploaded(String json){
        logger.info("Video uploaded event received.");
        VideoProcessingData data = JsonUtils.readJson(json, VideoProcessingData.class);
        logger.info("====RECEIVED VIDEO DATA====");
        logger.info("ID: "+data.videoId());
        logger.info("RAW FILE: "+data.rawFilePath());
        logger.info("************************************");
        logger.info("engaging converter...");
        converterService.convertToHls(
                data.rawFilePath(),
                data.videoId()
        );
    }

}
