package com.videoProcessor.app.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic videoConversionErrorTopic(){
        return TopicBuilder.name("video_conversion_error")
                .build();
    }

    @Bean
    public NewTopic videoProcessedTopic(){
        return TopicBuilder.name("video_processed")
                .build();
    }




}