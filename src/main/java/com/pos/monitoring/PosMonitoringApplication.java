package com.pos.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PosMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(PosMonitoringApplication.class, args);
	}

}
