package com.lonework.corners.seo.service;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.seo.model.SitemapEntry;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SitemapService {

    /** Maximum number of URLs allowed in a single sitemap by the sitemap protocol. */
    public static final int MAX_URLS = 50_000;

    private static final List<String> STATIC_PATHS = List.of("/", "/places", "/trips", "/events", "/cotravel");

    @Autowired
    EntityManager entityManager;

    @Transactional
    public List<SitemapEntry> getSitemapEntries() {
        List<SitemapEntry> entries = new ArrayList<>();
        STATIC_PATHS.forEach(path -> entries.add(new SitemapEntry(path, null)));

        for (Long id : findIds("select p.id from Place p order by p.id", MAX_URLS - entries.size())) {
            entries.add(new SitemapEntry("/places/" + id, null));
        }
        for (Long id : findIds("select t.id from Trip t order by t.id", MAX_URLS - entries.size())) {
            entries.add(new SitemapEntry("/trips/" + id, null));
        }
        int remaining = MAX_URLS - entries.size();
        if (remaining > 0) {
            List<Object[]> events = entityManager.createQuery(
                            "select e.id, e.createdAt from Event e where e.entityStatus = :status order by e.id",
                            Object[].class)
                    .setParameter("status", EntityStatus.ACTIVE)
                    .setMaxResults(remaining)
                    .getResultList();
            for (Object[] row : events) {
                ZonedDateTime createdAt = (ZonedDateTime) row[1];
                entries.add(new SitemapEntry("/events/" + row[0],
                        createdAt == null ? null : createdAt.withZoneSameInstant(ZoneOffset.UTC).toLocalDate()));
            }
        }
        return entries;
    }

    private List<Long> findIds(String jpql, int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return entityManager.createQuery(jpql, Long.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
