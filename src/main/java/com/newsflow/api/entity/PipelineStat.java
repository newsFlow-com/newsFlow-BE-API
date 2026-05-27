package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pipeline_stats",
        indexes = {
                @Index(name = "ix_ps_dag_started", columnList = "dag_id, started_at"),
                @Index(name = "ix_ps_status", columnList = "status"),
                @Index(name = "ix_ps_started", columnList = "started_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PipelineStat {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "dag_id", nullable = false, length = 100)
    private String dagId;

    @Column(name = "run_id", length = 255)
    private String runId;

    /** success | partial | fail | running */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "collected_count", nullable = false)
    private int collectedCount;

    @Column(name = "duplicate_count", nullable = false)
    private int duplicateCount;

    @Column(name = "error_count", nullable = false)
    private int errorCount;

    @Column(name = "source_count", nullable = false)
    private int sourceCount;

    @Column(name = "duration_seconds")
    private Double durationSeconds;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}