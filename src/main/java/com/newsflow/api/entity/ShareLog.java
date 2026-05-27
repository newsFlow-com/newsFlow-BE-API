package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "share_logs",
        indexes = {
                @Index(name = "ix_sl_user", columnList = "user_id"),
                @Index(name = "ix_sl_target", columnList = "target_type, target_id"),
                @Index(name = "ix_sl_channel", columnList = "channel"),
                @Index(name = "ix_sl_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** article | chart | stock */
    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_id", nullable = false, columnDefinition = "uuid")
    private UUID targetId;

    /** kakao | link | clipboard | twitter */
    @Column(nullable = false, length = 30)
    private String channel;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public ShareLog(User user, String targetType, UUID targetId,
                    String channel, String ipAddress) {
        this.user = user;
        this.targetType = targetType;
        this.targetId = targetId;
        this.channel = channel;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }
}