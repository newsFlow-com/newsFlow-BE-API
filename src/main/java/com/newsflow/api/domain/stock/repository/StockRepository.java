package com.newsflow.api.domain.stock.repository;

import com.newsflow.api.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByTicker(String ticker);

    @Query("""
            SELECT s FROM Stock s
            WHERE s.isActive = true
              AND s.market = :market
            ORDER BY s.name ASC
            """)
    List<Stock> findByMarket(@Param("market") String market);

    @Query("""
            SELECT s FROM Stock s
            WHERE s.isActive = true
              AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.ticker) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY s.name ASC
            """)
    List<Stock> searchByKeyword(
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable
    );
}