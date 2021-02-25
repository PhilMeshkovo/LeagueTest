package com.example.LeagueTest.service;

import com.example.LeagueTest.model.Price;
import com.example.LeagueTest.model.Product;
import com.example.LeagueTest.repo.PriceRepo;
import com.example.LeagueTest.repo.ProductRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProductService {

    private final PriceRepo priceRepo;
    private final ProductRepo productRepo;

    @Autowired
    public ProductService(PriceRepo priceRepo, ProductRepo productRepo) {
        this.priceRepo = priceRepo;
        this.productRepo = productRepo;
    }

    public List<ObjectNode> getProductsByDate(String date) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date localDate = dateFormatter.parse(date);
        List<Price> prices = priceRepo.getAllPricesByTime(localDate);
        List<ObjectNode> objectNodeList = new ArrayList<>();
        for (Price price : prices) {
            ObjectNode object = createObjectNode();
            String name = price.getProductId().getName();
            object.put("name", name);
            object.put("price", price.getPrice());
            objectNodeList.add(object);
        }
        return objectNodeList;
    }

    public ObjectNode getStatistics() throws InterruptedException {
        ObjectNode object = createObjectNode();
        ObjectMapper mapper = new ObjectMapper();
        object.put("count", productRepo.count());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Product> allProducts = productRepo.findAll();
                Map<String, Integer> productToFrequency = new HashMap<>();
                for (Product product : allProducts) {
                    List<Price> pricesForProduct = priceRepo.findAllByProductId(product.getId());
                    productToFrequency.put(product.getName(), pricesForProduct.size());
                }
                JsonNode map = mapper.valueToTree(productToFrequency);
                object.set("frequency", map);
            }
        });
        thread.start();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Date> dates = priceRepo.getAllDates();
                Map<String, Integer> dateToCount = new HashMap<>();
                for (Date date : dates) {
                    Integer count = priceRepo.getCountForDate(date);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = dateFormat.format(date);
                    dateToCount.put(strDate, count);
                }
                JsonNode mapDates = mapper.valueToTree(dateToCount);
                object.set("countToDates", mapDates);
            }
        });
        thread1.start();
        thread.join();
        thread1.join();

        return object;
    }

    private ObjectNode createObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode object = mapper.createObjectNode();
        return object;
    }


}
