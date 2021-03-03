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

    @Query(nativeQuery = true, value = "SELECT price.time, count(*)\n" +
            "\tFROM price GROUP BY price.time;")
    List<Object[]> getFrequencyToDates();
}
