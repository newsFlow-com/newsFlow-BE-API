package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "keywords",
        indexes = {
                @Index(name = "ix_keywords_normalized", columnList = "word_normalized"),
                @Index(name = "ix_keywords_language", columnList = "language_code"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String word;

    @Column(name = "word_normalized", length = 100)
    private String wordNormalized;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode = "ko";

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}