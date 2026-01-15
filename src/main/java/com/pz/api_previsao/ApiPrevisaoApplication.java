package com.pz.api_previsao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiPrevisaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiPrevisaoApplication.class, args);
	}

}
