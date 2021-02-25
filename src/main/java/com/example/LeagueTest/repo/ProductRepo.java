package com.example.LeagueTest.repo;

import com.example.LeagueTest.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
