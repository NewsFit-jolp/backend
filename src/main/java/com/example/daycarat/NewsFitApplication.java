package com.example.daycarat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NewsFitApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsFitApplication.class, args);
	}

}
