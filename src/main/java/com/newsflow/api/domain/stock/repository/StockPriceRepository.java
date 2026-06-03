package com.newsflow.api.domain.stock.repository;

import com.newsflow.api.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockPriceRepository extends JpaRepository<StockPrice, UUID> {

    /**
     * 특정 종목의 최근 N일 가격 조회 (차트용).
     */
    @Query("""
            SELECT p FROM StockPrice p
            WHERE p.stock.id = :stockId
              AND p.priceDate >= :from
            ORDER BY p.priceDate ASC
            """)
    List<StockPrice> findByStockIdAndDateRange(
            @Param("stockId") UUID stockId,
            @Param("from") LocalDate from
    );

    /**
     * 특정 종목의 최신 가격 1건.
     */
    @Query("""
            SELECT p FROM StockPrice p
            WHERE p.stock.id = :stockId
            ORDER BY p.priceDate DESC
            """)
    List<StockPrice> findLatestByStockId(
            @Param("stockId") UUID stockId,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 특정 종목의 특정 날짜 가격.
     */
    Optional<StockPrice> findByStockIdAndPriceDate(UUID stockId, LocalDate priceDate);
}