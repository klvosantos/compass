package com.example.compass;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class CompassApplication implements CommandLineRunner{
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

	@Autowired
	private SqsTemplate sqsTemplate;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(".");
//		var SQSPartial = "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/partial-queue";
//		sqsTemplate.send(SQSPartial, "Hello World!");
	}
}
