package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "content_quality_logs",
        indexes = {
                @Index(name = "ix_cql_article", columnList = "article_id"),
                @Index(name = "ix_cql_check_type", columnList = "check_type"),
                @Index(name = "ix_cql_is_correct", columnList = "is_correct"),
                @Index(name = "ix_cql_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentQualityLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    /** ai_category | duplicate | keyword | stock_link */
    @Column(name = "check_type", nullable = false, length = 30)
    private String checkType;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "original_value", columnDefinition = "jsonb")
    private Map<String, Object> originalValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "correction", columnDefinition = "jsonb")
    private Map<String, Object> correction;

    @Column(name = "checked_by", columnDefinition = "uuid")
    private UUID checkedBy;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}