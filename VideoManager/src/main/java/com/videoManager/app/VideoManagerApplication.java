package com.videoManager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableCaching
public class VideoManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoManagerApplication.class, args);
	}


	@Bean
	public RestClient restClient(RestClient.Builder builder){
		return builder.build();
	}

}
