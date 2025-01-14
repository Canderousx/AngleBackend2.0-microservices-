package com.videoProcessor.app.Services.Kafka;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaSenderService {

    private final KafkaTemplate<String,String>kafkaTemplate;

    private final Logger logger = LogManager.getLogger(KafkaSenderService.class);

    public KafkaSenderService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void send(String topic, String value){
        CompletableFuture<Void> sender = kafkaTemplate.send(
                        topic,value
                ).thenAccept(result -> {
                    logger.info("Message sent successfully. Topic: " + result.getRecordMetadata().topic()
                            + " Partition: " + result.getRecordMetadata().partition()
                            + " Offset: " + result.getRecordMetadata().offset());
                })
                .exceptionally(ex -> {
                    logger.error("Failed to send message: " + ex.getMessage());
                    return null;
                });

    }
}
