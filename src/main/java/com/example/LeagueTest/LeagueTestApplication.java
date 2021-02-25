package com.example.LeagueTest;

import com.example.LeagueTest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class LeagueTestApplication {


	private static ProductService productService;

	@Autowired
	public LeagueTestApplication(ProductService productService) {
		this.productService = productService;
	}

	public static void main(String[] args) {
		SpringApplication.run(LeagueTestApplication.class, args);
		Path path = Paths.get(productService.getFileName());

		if (Files.exists(path)){
			productService.parseFile();
		}
	}

}
