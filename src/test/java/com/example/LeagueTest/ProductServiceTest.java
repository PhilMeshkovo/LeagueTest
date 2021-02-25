package com.example.LeagueTest;

import com.example.LeagueTest.model.Price;
import com.example.LeagueTest.model.Product;
import com.example.LeagueTest.repo.PriceRepo;
import com.example.LeagueTest.service.ProductService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {

    @MockBean
    PriceRepo priceRepo;

    @Autowired
    ProductService productService;

    @Test
    public void getProductsByDateTest() throws ParseException {
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormatter.parse("2020-10-10");
        Price price = new Price();
        price.setId(1L);
        price.setPrice(4000.0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse("2020-10-10", dateFormatter);
        price.setTime(localDate);
        price.setProductId(new Product(1L, "product1"));
        Mockito.doReturn(List.of(price)).when(priceRepo).getAllPricesByTime(date);
        List<ObjectNode> productsByDate = productService.getProductsByDate("2020-10-10");
        Assertions.assertEquals(4000.0, productsByDate.get(0).get("price").asDouble());
    }
}
