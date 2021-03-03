package com.example.LeagueTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LeagueTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeagueTestApplication.class, args);
    }

}
