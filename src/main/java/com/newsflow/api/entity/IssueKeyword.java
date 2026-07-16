package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "issue_keywords",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_ik_issue_keyword",
                columnNames = {"issue_id", "keyword_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueKeyword {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Column(nullable = false)
    private double weight;
}
