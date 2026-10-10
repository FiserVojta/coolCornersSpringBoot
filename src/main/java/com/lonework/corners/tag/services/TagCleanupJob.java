package com.lonework.corners.tag.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Nightly removal of user-created tags that were never (or are no longer) used.
 * Disable with {@code tags.cleanup.enabled=false}.
 */
@Component
@ConditionalOnProperty(name = "tags.cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class TagCleanupJob {

    static final Duration MIN_AGE = Duration.ofDays(30);

    private static final Logger log = LoggerFactory.getLogger(TagCleanupJob.class);

    private final TagService tagService;

    public TagCleanupJob(TagService tagService) {
        this.tagService = tagService;
    }

    @Scheduled(cron = "${tags.cleanup.cron:0 30 3 * * *}")
    public void deleteUnusedUserTags() {
        int deleted = tagService.deleteUnusedUserTags(MIN_AGE);
        log.info("Tag cleanup deleted {} unused user tag(s) older than {} days", deleted, MIN_AGE.toDays());
    }
}
