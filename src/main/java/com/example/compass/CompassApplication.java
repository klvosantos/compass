package com.example.compass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class CompassApplication {
	private static final Logger log = LoggerFactory.getLogger(CompassApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(CompassApplication.class, args);
	}

	// SQl debug logs
	@Bean
	public CommandLineRunner initDatabase() {
		return args -> {
			String dataSql = FileCopyUtils.copyToString(new InputStreamReader(
					new ClassPathResource("data.sql").getInputStream(), StandardCharsets.UTF_8));
			log.info("data.sql contents:\n{}", dataSql);
		};
	}

}
