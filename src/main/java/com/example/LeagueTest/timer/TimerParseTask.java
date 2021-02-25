package com.example.LeagueTest.timer;

import com.example.LeagueTest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TimerTask;

public class TimerParseTask extends TimerTask {

    private final ProductService productService;

    @Autowired
    public TimerParseTask(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        Path path = Paths.get(productService.getFileName());
        if (Files.exists(path)) {
            productService.parseFile();
        }
    }
}
