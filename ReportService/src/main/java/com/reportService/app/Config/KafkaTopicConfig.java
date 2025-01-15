package com.reportService.app.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic newReportTopic(){
        return TopicBuilder.name("new_report")
                .build();
    }

    @Bean
    public NewTopic reportSolvedTopic(){
        return TopicBuilder.name("report_solved")
                .build();
    }



}