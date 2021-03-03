package com.example.LeagueTest.service;

import com.example.LeagueTest.dto.ProductsByDateDto;
import com.example.LeagueTest.dto.StatisticDto;
import com.example.LeagueTest.model.Price;
import com.example.LeagueTest.model.Product;
import com.example.LeagueTest.repo.PriceRepo;
import com.example.LeagueTest.repo.ProductRepo;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
@Data
public class ProductService {

    @Value("${directory.value}")
    private String directoryName;

    private final PriceRepo priceRepo;
    private final ProductRepo productRepo;

    @Autowired
    public ProductService(PriceRepo priceRepo, ProductRepo productRepo) {
        this.priceRepo = priceRepo;
        this.productRepo = productRepo;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduledParse() {
        File folder = new File(directoryName);
        for (File file : folder.listFiles()) {
            if (file.toString().endsWith(".csv")) {
                parseFile(file.getAbsolutePath());
            }
        }
    }

    public List<ProductsByDateDto> getProductsByDate(String date) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date localDate = dateFormatter.parse(date);
        List<Price> prices = priceRepo.getAllPricesByTime(localDate);
        List<ProductsByDateDto> productsList = new ArrayList<>();
        for (Price price : prices) {
            ProductsByDateDto productsByDateDto = new ProductsByDateDto();
            productsByDateDto.setName(price.getProductId().getName());
            productsByDateDto.setPrice(price.getPrice());
            productsList.add(productsByDateDto);
        }
        return productsList;
    }

    public StatisticDto getStatistics() throws InterruptedException {
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setCount(productRepo.count());


        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            List<Product> allProducts = productRepo.findAll();
            Map<String, Integer> productToFrequency = new HashMap<>();
            for (Product product : allProducts) {
                Integer pricesCountForProduct = priceRepo.findCountForProductId(product.getId());
                productToFrequency.put(product.getName(), pricesCountForProduct);
            }
            statisticDto.setFrequency(productToFrequency);
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            List<Date> dates = priceRepo.getAllDates();
            Map<String, Integer> dateToCount = new HashMap<>();
            for (Date date : dates) {
                Integer count = priceRepo.getCountForDate(date);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = dateFormat.format(date);
                dateToCount.put(strDate, count);
            }
            statisticDto.setCountToDates(dateToCount);
        });

        CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);
        try {
            future.get();
        } catch (ExecutionException e) {
            log.error(e.getMessage());
        }
        return statisticDto;
    }

    @Transactional
    public void parseFile(String fileName) {
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
        File file = new File(fileName);
        file.delete();
        log.info("File deleted");
    }
}
