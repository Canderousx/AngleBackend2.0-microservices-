package com.reportService.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class ReportServiceApplication {

	@Bean
	public RestClient restClient(RestClient.Builder builder){
		return builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

}
