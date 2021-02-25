package com.example.LeagueTest;

import com.example.LeagueTest.service.ProductService;
import com.example.LeagueTest.timer.TimerParseTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;

@SpringBootApplication
public class LeagueTestApplication {


    private static ProductService productService;

    @Autowired
    public LeagueTestApplication(ProductService productService) {
        this.productService = productService;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(LeagueTestApplication.class, args);
        new Timer().scheduleAtFixedRate(new TimerParseTask(productService), 0, 5000);

        for (int i = 0; i < 3; i++) {
            Thread.sleep(5000);
        }
    }

}
