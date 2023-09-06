package com.pos.monitoring;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition
public class PosMonitoringApplication {
    public static String schedule = "3c6,3c7,01200,9k4";

    public static void main(String[] args) {
        SpringApplication.run(PosMonitoringApplication.class, args);
    }

}
