package com.example.LeagueTest.service;

import com.example.LeagueTest.model.Price;
import com.example.LeagueTest.model.Product;
import com.example.LeagueTest.repo.PriceRepo;
import com.example.LeagueTest.repo.ProductRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Log4j2
@Data
public class ProductService {

    @Value("${fileName.value}")
    private String fileName;

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
            object.put("name", price.getProductId().getName());
            object.put("price", price.getPrice());
            objectNodeList.add(object);
        }
        return objectNodeList;
    }

    public ObjectNode getStatistics() throws InterruptedException {
        ObjectNode object = createObjectNode();
        ObjectMapper mapper = new ObjectMapper();
        object.put("count", productRepo.count());

        Thread thread = new Thread(() -> {
            List<Product> allProducts = productRepo.findAll();
            Map<String, Integer> productToFrequency = new HashMap<>();
            for (Product product : allProducts) {
                Integer pricesCountForProduct = priceRepo.findCountForProductId(product.getId());
                productToFrequency.put(product.getName(), pricesCountForProduct);
            }
            JsonNode map = mapper.valueToTree(productToFrequency);
            object.set("frequency", map);
        });
        thread.start();

        Thread thread1 = new Thread(() -> {
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
        });
        thread1.start();
        thread.join();
        thread1.join();

        return object;
    }

    @Transactional
    public void parseFile() {
        log.info("Begin parsing file");
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(fileName))
                .withCSVParser(csvParser)
                .withSkipLines(1)
                .build()) {
            List<String[]> listArrays = reader.readAll();
            for (String[] listArray : listArrays) {
                Product product = new Product();
                product.setName(listArray[1]);
                Product savedProduct = productRepo.save(product);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(listArray[4], dateFormatter);
                Price price = new Price();
                price.setPrice(Double.parseDouble(listArray[3]));
                price.setTime(localDate);
                price.setProductId(savedProduct);
                priceRepo.save(price);
            }
            listArrays.forEach(x -> log.info(Arrays.toString(x)));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (CsvException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }


}
