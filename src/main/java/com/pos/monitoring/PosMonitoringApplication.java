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
    public static void main(String[] args) {
        SpringApplication.run(PosMonitoringApplication.class, args);
    }

}
