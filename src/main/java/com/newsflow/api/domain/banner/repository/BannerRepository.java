package com.newsflow.api.domain.banner.repository;

import com.newsflow.api.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BannerRepository extends JpaRepository<Banner, UUID> {

    @Query("""
            SELECT b FROM Banner b
            WHERE b.isActive = true
              AND b.position = :position
              AND (b.startAt IS NULL OR b.startAt <= :now)
              AND (b.endAt IS NULL OR b.endAt > :now)
            ORDER BY b.displayOrder ASC
            """)
    List<Banner> findActiveByPosition(@Param("position") String position,
                                      @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Banner b
            WHERE b.isActive = true
              AND (b.startAt IS NULL OR b.startAt <= :now)
              AND (b.endAt IS NULL OR b.endAt > :now)
            ORDER BY b.position ASC, b.displayOrder ASC
            """)
    List<Banner> findAllActive(@Param("now") LocalDateTime now);
}
