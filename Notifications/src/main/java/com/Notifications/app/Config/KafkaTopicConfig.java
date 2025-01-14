package com.Notifications.app.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic videoUploadedTopic(){
        return TopicBuilder.name("video_uploaded")
                .build();
    }

    @Bean
    public NewTopic newMetadataSet(){
        return TopicBuilder.name("new_video_metadata")
                .build();
    }

    @Bean
    public NewTopic deleteVideoTopic(){
        return TopicBuilder.name("delete_video")
                .build();
    }



}