package com.example.LeagueTest.controller;

import com.example.LeagueTest.service.ProductService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getProductForDate(@RequestParam(value = "date") String date) throws ParseException {
        List<ObjectNode> productsByDate = productService.getProductsByDate(date);
        return ResponseEntity.ok(productsByDate);
    }

    @GetMapping("/statistic")
    public ResponseEntity<?> getStatistics() throws InterruptedException {
        ObjectNode statistics = productService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
//    @GetMapping("/parse")
//    public void parseFile() throws ParseException {
//        productService.parseFile();
//    }
}
