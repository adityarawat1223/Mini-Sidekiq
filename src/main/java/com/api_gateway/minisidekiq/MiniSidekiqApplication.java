package com.api_gateway.minisidekiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniSidekiqApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniSidekiqApplication.class, args);
    }

}
