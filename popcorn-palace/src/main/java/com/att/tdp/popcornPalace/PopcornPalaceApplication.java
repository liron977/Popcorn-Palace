package com.att.tdp.popcornPalace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.att.tdp.popcornPalace")
@EnableJpaRepositories(basePackages = "com.att.tdp.popcornPalace.repositories")
@EntityScan(basePackages = "com.att.tdp.popcornPalace.models")
public class PopcornPalaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopcornPalaceApplication.class, args);
	}

}
