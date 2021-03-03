package com.example.LeagueTest.timer;

import com.example.LeagueTest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.TimerTask;

public class TimerParseTask extends TimerTask {

    private final ProductService productService;

    @Autowired
    public TimerParseTask(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        File folder = new File(productService.getDirectoryName());
        for (File file : folder.listFiles()) {
            if (file.toString().endsWith(".csv")) {
                productService.parseFile(file.getAbsolutePath());
            }
        }
    }
}
