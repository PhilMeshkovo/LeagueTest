package com.example.LeagueTest.repo;

import com.example.LeagueTest.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, value = "SELECT product.name, count(price)\n" +
            "\tFROM product JOIN price ON product.id = price.product_id GROUP BY product.name;")
    List<Object[]> getCountToProduct();
}
