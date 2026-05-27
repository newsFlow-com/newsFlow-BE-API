package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collect_logs",
        indexes = {
                @Index(name = "ix_cl_source_started", columnList = "source_id, started_at"),
                @Index(name = "ix_cl_status", columnList = "status"),
                @Index(name = "ix_cl_started", columnList = "started_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    /** success | fail | partial */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "collected_count", nullable = false)
    private int collectedCount;

    @Column(name = "duplicate_count", nullable = false)
    private int duplicateCount;

    @Column(name = "error_count", nullable = false)
    private int errorCount;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}