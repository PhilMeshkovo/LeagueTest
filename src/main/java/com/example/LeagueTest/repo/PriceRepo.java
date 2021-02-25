package com.example.LeagueTest.repo;

import com.example.LeagueTest.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PriceRepo extends JpaRepository<Price, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM price WHERE time = :date")
    List<Price> getAllPricesByTime(@Param("date") Date date);

    @Query(nativeQuery = true,
            value = "SELECT * FROM price WHERE product_id = :productId")
    List<Price> findAllByProductId(@Param("productId")Long productId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT price.time FROM price")
    List<Date> getAllDates();

    @Query(nativeQuery = true,
            value = "SELECT count(*) FROM price WHERE time = :date")
    Integer getCountForDate(@Param("date") Date date);
}
