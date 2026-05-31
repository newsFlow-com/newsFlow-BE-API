package com.newsflow.api.common.scheduler;

import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ViewCountSyncScheduler {

    private static final String VIEW_COUNT_KEY_PREFIX = "article:view:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ArticleRepository articleRepository;

    /**
     * 매 5분마다 Redis 조회수를 DB에 동기화.
     * 1. Redis에서 article:view:* 키 전체 조회
     * 2. 각 키의 값(누적 조회수)을 DB view_count에 반영
     * 3. Redis 키 삭제 (다음 배치까지 초기화)
     */
    @Scheduled(fixedDelay = 300_000)  // 5분
    @Transactional
    public void syncViewCounts() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        int syncCount = 0;
        int errorCount = 0;

        for (String key : keys) {
            try {
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) continue;

                long increment = Long.parseLong(value);
                if (increment <= 0) continue;

                String articleIdStr = key.replace(VIEW_COUNT_KEY_PREFIX, "");
                UUID articleId = UUID.fromString(articleIdStr);

                articleRepository.findById(articleId).ifPresent(article -> {
                    article.addViewCount((int) increment);
                    articleRepository.save(article);
                });

                redisTemplate.delete(key);
                syncCount++;

            } catch (Exception e) {
                log.error("[ViewCountSync] 동기화 실패: key={}, error={}", key, e.getMessage());
                errorCount++;
            }
        }

        if (syncCount > 0 || errorCount > 0) {
            log.info("[ViewCountSync] 완료 — 동기화: {}건, 실패: {}건", syncCount, errorCount);
        }
    }
}