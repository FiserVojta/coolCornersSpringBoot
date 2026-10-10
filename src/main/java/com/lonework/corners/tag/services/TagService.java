package com.lonework.corners.tag.services;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagAdminResponse;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagCreationLimitException;
import com.lonework.corners.tag.model.TagNames;
import com.lonework.corners.tag.model.TagSearchRequest;
import com.lonework.corners.tag.model.TagStatus;
import com.lonework.corners.tag.model.TagSuggestion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Configurable
public class TagService {

    static final int MAX_NAME_LENGTH = 50;
    static final int MIN_NORMALIZED_LENGTH = 2;
    static final int DEFAULT_SEARCH_LIMIT = 10;
    static final int MAX_SEARCH_LIMIT = 50;
    static final int MAX_RESOLVE_NAMES = 20;
    static final int MAX_NEW_TAGS_PER_DAY = 20;
    static final int DEFAULT_ADMIN_PAGE_SIZE = 20;
    static final int MAX_ADMIN_PAGE_SIZE = 100;

    private static final String USAGE_COUNT_SQL =
            "(SELECT count(*) FROM place_has_tag x WHERE x.tag_id = t.id)"
                    + " + (SELECT count(*) FROM trip_has_tag x WHERE x.tag_id = t.id)"
                    + " + (SELECT count(*) FROM wander_has_tag x WHERE x.tag_id = t.id)"
                    + " + (SELECT count(*) FROM travel_has_tag x WHERE x.tag_id = t.id)";

    private static final String ADMIN_SELECT_SQL =
            "SELECT t.id, t.name, t.normalized_name, t.creator, t.status, t.created_at, "
                    + USAGE_COUNT_SQL + " AS usage_count\n"
                    + "FROM tag t\n";

    /** The four tag join tables and their owner column, used by merge. */
    private static final List<String[]> TAG_JOIN_TABLES = List.of(
            new String[]{"place_has_tag", "place_id"},
            new String[]{"trip_has_tag", "trip_id"},
            new String[]{"wander_has_tag", "wander_id"},
            new String[]{"travel_has_tag", "travel_id"});

    private static final String DELETE_UNUSED_USER_TAGS_SQL = """
            DELETE FROM tag t
            WHERE t.status = 'ACTIVE'
              AND t.creator <> 'system'
              AND t.created_at < now() - (:minAgeSeconds * interval '1 second')
              AND NOT EXISTS (SELECT 1 FROM place_has_tag x WHERE x.tag_id = t.id)
              AND NOT EXISTS (SELECT 1 FROM trip_has_tag x WHERE x.tag_id = t.id)
              AND NOT EXISTS (SELECT 1 FROM wander_has_tag x WHERE x.tag_id = t.id)
              AND NOT EXISTS (SELECT 1 FROM travel_has_tag x WHERE x.tag_id = t.id)
            """;

    /*
     * Usage is counted with correlated subqueries so only matched tags are counted; each join table
     * has an index leading with tag_id. An empty :query matches every tag (LIKE '%%') and every tag
     * then falls into the same prefix rank, so the blank-query case shares this statement.
     */
    private static final String SEARCH_TAGS_SQL = """
            SELECT t.id,
                   t.name,
                   t.normalized_name,
                   (SELECT count(*) FROM place_has_tag x WHERE x.tag_id = t.id)
                 + (SELECT count(*) FROM trip_has_tag x WHERE x.tag_id = t.id)
                 + (SELECT count(*) FROM wander_has_tag x WHERE x.tag_id = t.id)
                 + (SELECT count(*) FROM travel_has_tag x WHERE x.tag_id = t.id) AS usage_count
            FROM tag t
            WHERE t.status <> 'HIDDEN'
              AND t.normalized_name LIKE '%' || :query || '%'
            ORDER BY CASE WHEN t.normalized_name LIKE :query || '%' THEN 0 ELSE 1 END,
                     usage_count DESC,
                     lower(t.name) ASC,
                     t.id ASC
            LIMIT :limit
            """;

    @Autowired
    EntityManager entityManager;


    /**
     * Returns distinct tags attached to any of the given places or any of the given trips.
     * Null or empty id lists are ignored; when both are empty, an empty list is returned.
     */
    public List<Tag> getTagsWithRequest(TagSearchRequest tagSearchRequest) {
        List<Long> placeIds = tagSearchRequest == null ? null : tagSearchRequest.placeId();
        List<Long> tripIds = tagSearchRequest == null ? null : tagSearchRequest.tripId();
        boolean hasPlaces = placeIds != null && !placeIds.isEmpty();
        boolean hasTrips = tripIds != null && !tripIds.isEmpty();
        if (!hasPlaces && !hasTrips) {
            return List.of();
        }

        List<String> conditions = new ArrayList<>();
        if (hasPlaces) {
            conditions.add("t.id IN (SELECT pt.id FROM Place p JOIN p.tags pt WHERE p.id IN :placeIds)");
        }
        if (hasTrips) {
            conditions.add("t.id IN (SELECT trt.id FROM Trip tr JOIN tr.tags trt WHERE tr.id IN :tripIds)");
        }

        TypedQuery<Tag> query = entityManager.createQuery(
                "SELECT t FROM Tag t WHERE " + String.join(" OR ", conditions), Tag.class);
        if (hasPlaces) {
            query.setParameter("placeIds", placeIds);
        }
        if (hasTrips) {
            query.setParameter("tripIds", tripIds);
        }
        return query.getResultList();
    }

    /**
     * Find-or-create a shared tag. If a tag with the same normalized name already exists it is returned
     * unchanged (its original display name and creator are kept).
     */
    @Transactional
    public Tag createTag(TagCreateRequest tagCreateRequest, String creator) {
        if (creator == null || creator.isBlank()) {
            throw new IllegalArgumentException("Tag creator is required.");
        }
        String rawName = tagCreateRequest == null ? null : tagCreateRequest.name();
        if (rawName == null) {
            throw new IllegalArgumentException("Tag name is required.");
        }
        String displayName = TagNames.displayName(rawName);
        if (displayName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Tag name must be at most " + MAX_NAME_LENGTH + " characters.");
        }
        String normalizedName = TagNames.normalize(displayName);
        if (normalizedName.length() < MIN_NORMALIZED_LENGTH) {
            throw new IllegalArgumentException(
                    "Tag name must contain at least " + MIN_NORMALIZED_LENGTH + " letters or digits.");
        }
        if (normalizedName.length() > MAX_NAME_LENGTH) {
            // NFKC can expand some characters (e.g. ligatures); keep within the column size.
            throw new IllegalArgumentException("Tag name must be at most " + MAX_NAME_LENGTH + " characters.");
        }

        List<Tag> existing = entityManager
                .createQuery("SELECT t FROM Tag t WHERE t.normalizedName = :normalizedName", Tag.class)
                .setParameter("normalizedName", normalizedName)
                .setMaxResults(1)
                .getResultList();
        if (!existing.isEmpty()) {
            Tag found = existing.get(0);
            if (found.getStatus() == TagStatus.HIDDEN) {
                throw new IllegalArgumentException("This tag is not available.");
            }
            return found;
        }

        // Only genuinely new tags count towards the daily limit; reusing an existing tag is free.
        long createdRecently = entityManager.createQuery(
                        "SELECT count(t) FROM Tag t WHERE t.creator = :creator AND t.createdAt > :since", Long.class)
                .setParameter("creator", creator)
                .setParameter("since", ZonedDateTime.now().minusHours(24))
                .getSingleResult();
        if (createdRecently >= MAX_NEW_TAGS_PER_DAY) {
            throw new TagCreationLimitException("You've created too many tags today. Try again tomorrow.");
        }

        Tag tag = new Tag();
        tag.setName(displayName);
        tag.setCreator(creator);
        entityManager.persist(tag);
        return tag;
    }

    /**
     * Autocomplete search over tags. The query is normalized like tag names; a blank or missing query
     * returns the most used tags. Prefix matches rank before other substring matches.
     */
    public List<TagSuggestion> searchTags(String q, Integer limit) {
        int effectiveLimit = limit == null || limit < 1 ? DEFAULT_SEARCH_LIMIT : Math.min(limit, MAX_SEARCH_LIMIT);
        String normalizedQuery = q == null ? null : TagNames.normalize(q.strip());
        if (normalizedQuery == null) {
            normalizedQuery = "";
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(SEARCH_TAGS_SQL)
                .setParameter("query", normalizedQuery)
                .setParameter("limit", effectiveLimit)
                .getResultList();

        return rows.stream()
                .map(row -> new TagSuggestion(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue()))
                .toList();
    }

    /**
     * Resolves tag names to existing tags by their normalized name. Inputs are trimmed and normalized,
     * blanks and duplicates are dropped, unknown names are ignored, and results follow input order.
     * At most the first {@value #MAX_RESOLVE_NAMES} distinct names are resolved. Hidden tags are
     * included so that existing tag links keep filtering.
     */
    public List<Tag> getTagsByNormalizedNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        Set<String> normalizedNames = new LinkedHashSet<>();
        for (String name : names) {
            String normalized = name == null ? null : TagNames.normalize(name.strip());
            if (normalized != null && !normalized.isEmpty()) {
                normalizedNames.add(normalized);
                if (normalizedNames.size() >= MAX_RESOLVE_NAMES) {
                    break;
                }
            }
        }
        if (normalizedNames.isEmpty()) {
            return List.of();
        }

        Map<String, Tag> tagsByNormalizedName = entityManager
                .createQuery("SELECT t FROM Tag t WHERE t.normalizedName IN :names", Tag.class)
                .setParameter("names", normalizedNames)
                .getResultStream()
                .collect(Collectors.toMap(Tag::getNormalizedName, Function.identity(), (first, second) -> first));

        return normalizedNames.stream()
                .map(tagsByNormalizedName::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Tag> getAllTags() {
        return entityManager.createQuery("SELECT t FROM Tag t WHERE t.status <> :hidden", Tag.class)
                .setParameter("hidden", TagStatus.HIDDEN)
                .getResultList();
    }

    // --- Admin moderation ----------------------------------------------------

    /**
     * Admin listing of all tags (including hidden ones) with usage counts. {@code q} is normalized and
     * matched as a substring of the normalized name (blank = all); {@code status} null = any status.
     * Ordered by usage desc, then name, then id.
     */
    public PagedResult<TagAdminResponse> getTagsForAdmin(String q, TagStatus status, PagingQueryParams paging) {
        String normalizedQuery = q == null ? "" : TagNames.normalize(q.strip());
        int size = paging == null || paging.size() == null || paging.size() < 1
                ? DEFAULT_ADMIN_PAGE_SIZE
                : Math.min(paging.size(), MAX_ADMIN_PAGE_SIZE);
        int page = paging == null || paging.page() == null || paging.page() < 0 ? 0 : paging.page();

        List<String> conditions = new ArrayList<>();
        if (!normalizedQuery.isEmpty()) {
            conditions.add("t.normalized_name LIKE '%' || :query || '%'");
        }
        if (status != null) {
            conditions.add("t.status = :status");
        }
        String where = conditions.isEmpty() ? "" : "WHERE " + String.join(" AND ", conditions) + "\n";

        Query dataQuery = entityManager.createNativeQuery(ADMIN_SELECT_SQL + where + """
                ORDER BY usage_count DESC, lower(t.name) ASC, t.id ASC
                LIMIT :limit OFFSET :offset
                """);
        Query countQuery = entityManager.createNativeQuery("SELECT count(*) FROM tag t " + where);
        if (!normalizedQuery.isEmpty()) {
            dataQuery.setParameter("query", normalizedQuery);
            countQuery.setParameter("query", normalizedQuery);
        }
        if (status != null) {
            dataQuery.setParameter("status", status.name());
            countQuery.setParameter("status", status.name());
        }
        dataQuery.setParameter("limit", size);
        dataQuery.setParameter("offset", (long) page * size);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PagedResult<>(rows.stream().map(TagService::toAdminResponse).toList(), total);
    }

    @Transactional
    public TagAdminResponse setTagStatus(Long id, TagStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Tag status is required.");
        }
        Tag tag = requireTag(id);
        tag.setStatus(status);
        entityManager.flush();
        return getAdminResponse(tag.getId());
    }

    /**
     * Merges {@code sourceId} into {@code targetId}: every place/trip/wander/travel link of the source is
     * re-pointed to the target (skipping links the target already has), then the source tag is deleted.
     */
    @Transactional
    public TagAdminResponse mergeTags(Long sourceId, Long targetId) {
        if (sourceId == null || targetId == null) {
            throw new IllegalArgumentException("Source and target tag ids are required.");
        }
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("A tag cannot be merged into itself.");
        }
        requireTag(sourceId);
        requireTag(targetId);
        // Push pending changes before bypassing the persistence context with native SQL.
        entityManager.flush();

        for (String[] joinTable : TAG_JOIN_TABLES) {
            String table = joinTable[0];
            String ownerColumn = joinTable[1];
            entityManager.createNativeQuery(
                            "INSERT INTO " + table + " (" + ownerColumn + ", tag_id) "
                                    + "SELECT " + ownerColumn + ", :targetId FROM " + table + " WHERE tag_id = :sourceId "
                                    + "ON CONFLICT DO NOTHING")
                    .setParameter("targetId", targetId)
                    .setParameter("sourceId", sourceId)
                    .executeUpdate();
            entityManager.createNativeQuery("DELETE FROM " + table + " WHERE tag_id = :sourceId")
                    .setParameter("sourceId", sourceId)
                    .executeUpdate();
        }
        entityManager.createNativeQuery("DELETE FROM tag WHERE id = :sourceId")
                .setParameter("sourceId", sourceId)
                .executeUpdate();
        // Loaded entities and tag collections are now stale.
        entityManager.clear();

        return getAdminResponse(targetId);
    }

    /**
     * Deletes ACTIVE, non-system tags older than {@code minAge} that are not attached to anything.
     *
     * @return the number of deleted tags
     */
    @Transactional
    public int deleteUnusedUserTags(Duration minAge) {
        if (minAge == null || minAge.isNegative()) {
            throw new IllegalArgumentException("A non-negative minimum age is required.");
        }
        entityManager.flush();
        return entityManager.createNativeQuery(DELETE_UNUSED_USER_TAGS_SQL)
                .setParameter("minAgeSeconds", minAge.toSeconds())
                .executeUpdate();
    }

    private Tag requireTag(Long id) {
        Tag tag = id == null ? null : entityManager.find(Tag.class, id);
        if (tag == null) {
            throw new EntityNotFoundException("Tag not found: " + id);
        }
        return tag;
    }

    private TagAdminResponse getAdminResponse(Long id) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(ADMIN_SELECT_SQL + "WHERE t.id = :id")
                .setParameter("id", id)
                .getResultList();
        if (rows.isEmpty()) {
            throw new EntityNotFoundException("Tag not found: " + id);
        }
        return toAdminResponse(rows.get(0));
    }

    private static TagAdminResponse toAdminResponse(Object[] row) {
        return new TagAdminResponse(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                TagStatus.valueOf((String) row[4]),
                toZonedDateTime(row[5]),
                ((Number) row[6]).longValue());
    }

    private static ZonedDateTime toZonedDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof ZonedDateTime zoned) {
            return zoned;
        }
        if (value instanceof OffsetDateTime offset) {
            return offset.toZonedDateTime();
        }
        if (value instanceof Instant instant) {
            return instant.atZone(ZoneOffset.UTC);
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().atZone(ZoneOffset.UTC);
        }
        throw new IllegalStateException("Unexpected timestamp type: " + value.getClass());
    }

}
