package com.lonework.corners.travel.service;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.files.api.FileOperations;
import com.lonework.corners.tag.api.TagOperations;
import com.lonework.corners.travel.model.Travel;
import com.lonework.corners.travel.model.TravelCreateRequest;
import com.lonework.corners.travel.model.TravelDayNote;
import com.lonework.corners.travel.model.TravelDayNoteRequest;
import com.lonework.corners.travel.model.TravelPhoto;
import com.lonework.corners.travel.model.TravelPhotoRequest;
import com.lonework.corners.travel.model.TravelPlace;
import com.lonework.corners.travel.model.TravelPlaceRequest;
import com.lonework.corners.travel.model.TravelRating;
import com.lonework.corners.travel.model.TravelVisibility;
import com.lonework.corners.user.api.UserOperations;
import com.lonework.corners.user.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Configurable
@Transactional
public class TravelService {

    private final EntityManager entityManager;
    private final UserOperations userOperations;
    private final FileOperations fileOperations;
    private final TagOperations tagOperations;

    public TravelService(EntityManager entityManager, UserOperations userOperations,
                         FileOperations fileOperations, TagOperations tagOperations) {
        this.entityManager = entityManager;
        this.userOperations = userOperations;
        this.fileOperations = fileOperations;
        this.tagOperations = tagOperations;
    }

    public Travel createTravel(TravelCreateRequest request, String ownerEmail) {
        User owner = userOperations.getRequiredUserByEmail(ownerEmail);

        Travel travel = new Travel();
        travel.setOwner(owner);
        travel.setShareToken(UUID.randomUUID().toString());
        travel.setEntityStatus(EntityStatus.ACTIVE);
        travel.setCreatedAt(ZonedDateTime.now());
        applyRequest(travel, request);

        entityManager.persist(travel);
        entityManager.flush();
        return travel;
    }

    @Transactional
    public Travel updateTravel(Long travelId, TravelCreateRequest request) {
        Travel travel = requireTravel(travelId);
        applyRequest(travel, request);
        return travel;
    }

    @Transactional
    public Travel updateVisibility(Long travelId, TravelVisibility visibility) {
        Travel travel = requireTravel(travelId);
        travel.setVisibility(visibility != null ? visibility : TravelVisibility.PRIVATE);
        return travel;
    }

    @Transactional
    public void deleteTravel(Long travelId) {
        Travel travel = requireTravel(travelId);
        entityManager.remove(travel);
    }

    public List<Travel> getMyTravels(String ownerEmail) {
        return entityManager.createQuery(
                        "SELECT t FROM Travel t WHERE t.owner.email = :email ORDER BY t.createdAt DESC",
                        Travel.class)
                .setParameter("email", ownerEmail)
                .getResultList();
    }

    /**
     * Travels the viewer is allowed to see, newest first: their own, all PUBLIC ones,
     * and FOLLOWERS ones from owners the viewer follows. An anonymous viewer
     * (null email) gets PUBLIC travels only.
     */
    public List<Travel> getTravelsForViewer(String viewerEmail) {
        if (viewerEmail == null) {
            return entityManager.createQuery(
                            "SELECT t FROM Travel t WHERE t.visibility = :publicVisibility ORDER BY t.createdAt DESC",
                            Travel.class)
                    .setParameter("publicVisibility", TravelVisibility.PUBLIC)
                    .getResultList();
        }
        return entityManager.createQuery(
                        "SELECT t FROM Travel t WHERE t.owner.email = :email"
                                + " OR t.visibility = :publicVisibility"
                                + " OR (t.visibility = :followersVisibility AND EXISTS ("
                                + "SELECT f FROM User o JOIN o.followers f WHERE o = t.owner AND f.email = :email))"
                                + " ORDER BY t.createdAt DESC",
                        Travel.class)
                .setParameter("email", viewerEmail)
                .setParameter("publicVisibility", TravelVisibility.PUBLIC)
                .setParameter("followersVisibility", TravelVisibility.FOLLOWERS)
                .getResultList();
    }

    /**
     * Returns a travel for a viewer, enforcing its visibility. An anonymous viewer
     * (null email) only sees PUBLIC travels.
     * Throws {@link EntityNotFoundException} (404) when the viewer is not allowed to see it,
     * so the existence of private travels is never disclosed.
     */
    public Travel getTravelForViewer(Long travelId, String viewerEmail) {
        Travel travel = requireTravel(travelId);
        if (!canView(travel, viewerEmail)) {
            throw new EntityNotFoundException("Travel not found with id: " + travelId);
        }
        return travel;
    }

    /**
     * Returns a travel by its share token, bypassing visibility entirely:
     * anyone holding the (unguessable) link may view it.
     */
    public Travel getByShareToken(String shareToken) {
        return entityManager.createQuery(
                        "SELECT t FROM Travel t WHERE t.shareToken = :token", Travel.class)
                .setParameter("token", shareToken)
                .getResultList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Travel not found for the provided link"));
    }

    public PagedResult<Travel> getPublicTravels(PagingQueryParams queryParams) {
        int page = queryParams != null && queryParams.page() != null ? queryParams.page() : 0;
        int size = queryParams != null && queryParams.size() != null ? queryParams.size() : 10;

        long total = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Travel t WHERE t.visibility = :visibility", Long.class)
                .setParameter("visibility", TravelVisibility.PUBLIC)
                .getSingleResult();

        List<Travel> travels = entityManager.createQuery(
                        "SELECT t FROM Travel t WHERE t.visibility = :visibility ORDER BY t.createdAt DESC",
                        Travel.class)
                .setParameter("visibility", TravelVisibility.PUBLIC)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return new PagedResult<>(travels, total);
    }

    /**
     * Travels of a given user that the viewer may see: all of them when the viewer is
     * that user, PUBLIC + FOLLOWERS for a follower, PUBLIC only for strangers and
     * anonymous viewers (null email).
     */
    public List<Travel> getUserTravelsForViewer(Long userId, String viewerEmail) {
        if (viewerEmail == null) {
            return getPublicUserTravels(userId);
        }
        return entityManager.createQuery(
                        "SELECT t FROM Travel t WHERE t.owner.id = :userId AND (t.owner.email = :email"
                                + " OR t.visibility = :publicVisibility"
                                + " OR (t.visibility = :followersVisibility AND EXISTS ("
                                + "SELECT f FROM User o JOIN o.followers f WHERE o = t.owner AND f.email = :email)))"
                                + " ORDER BY t.createdAt DESC",
                        Travel.class)
                .setParameter("userId", userId)
                .setParameter("email", viewerEmail)
                .setParameter("publicVisibility", TravelVisibility.PUBLIC)
                .setParameter("followersVisibility", TravelVisibility.FOLLOWERS)
                .getResultList();
    }

    /**
     * Public travels of a given user (visibility PUBLIC only). Used by the anonymous public controller.
     */
    public List<Travel> getPublicUserTravels(Long userId) {
        return entityManager.createQuery(
                        "SELECT t FROM Travel t WHERE t.owner.id = :userId AND t.visibility = :visibility "
                                + "ORDER BY t.createdAt DESC", Travel.class)
                .setParameter("userId", userId)
                .setParameter("visibility", TravelVisibility.PUBLIC)
                .getResultList();
    }

    /**
     * Upserts the viewer's rating (1-5) of a travel they can see and refreshes the
     * travel's average rating. Visibility is enforced the same way as viewing, so a
     * travel that is hidden from the viewer also cannot be rated (or probed) by them.
     */
    @Transactional
    public Travel rateTravel(Long travelId, String viewerEmail, Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Travel travel = getTravelForViewer(travelId, viewerEmail);

        TravelRating existing = findViewerRating(travelId, viewerEmail);
        if (existing == null) {
            existing = new TravelRating();
            existing.setTravelId(travelId);
            existing.setAuthor(viewerEmail);
            existing.setCreatedAt(ZonedDateTime.now());
            existing.setRating(rating);
            entityManager.persist(existing);
        } else {
            existing.setRating(rating);
        }
        entityManager.flush();

        travel.setRating(averageRating(travelId));
        return travel;
    }

    /**
     * The viewer's own rating of a travel, or null when they haven't rated it (or are anonymous).
     */
    public Integer getViewerRating(Long travelId, String viewerEmail) {
        if (viewerEmail == null) {
            return null;
        }
        TravelRating rating = findViewerRating(travelId, viewerEmail);
        return rating != null ? rating.getRating() : null;
    }

    private TravelRating findViewerRating(Long travelId, String viewerEmail) {
        return entityManager.createQuery(
                        "SELECT r FROM TravelRating r WHERE r.travelId = :travelId AND r.author = :author",
                        TravelRating.class)
                .setParameter("travelId", travelId)
                .setParameter("author", viewerEmail)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Double averageRating(Long travelId) {
        return entityManager.createQuery(
                        "SELECT AVG(r.rating) FROM TravelRating r WHERE r.travelId = :travelId", Double.class)
                .setParameter("travelId", travelId)
                .getSingleResult();
    }

    public boolean isOwner(Long travelId, String email) {
        if (travelId == null || email == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Travel t WHERE t.id = :id AND t.owner.email = :email", Long.class)
                .setParameter("id", travelId)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    private boolean canView(Travel travel, String viewerEmail) {
        if (travel.getVisibility() == TravelVisibility.PUBLIC) {
            return true;
        }
        if (viewerEmail == null) {
            return false;
        }
        if (viewerEmail.equals(travel.getOwner().getEmail())) {
            return true;
        }
        return travel.getVisibility() == TravelVisibility.FOLLOWERS
                && isFollowing(travel.getOwner().getId(), viewerEmail);
    }

    /**
     * True when the viewer (by email) is a follower of the given owner.
     * The {@code corneruser_followers} join exposes an owner's followers via {@code User.followers}.
     */
    private boolean isFollowing(Long ownerId, String viewerEmail) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(f) FROM User owner JOIN owner.followers f "
                                + "WHERE owner.id = :ownerId AND f.email = :viewerEmail", Long.class)
                .setParameter("ownerId", ownerId)
                .setParameter("viewerEmail", viewerEmail)
                .getSingleResult();
        return count > 0;
    }

    private Travel requireTravel(Long travelId) {
        Travel travel = entityManager.find(Travel.class, travelId);
        if (travel == null) {
            throw new EntityNotFoundException("Travel not found with id: " + travelId);
        }
        return travel;
    }

    private void applyRequest(Travel travel, TravelCreateRequest request) {
        travel.setTitle(request.title());
        travel.setDescription(request.description());
        travel.setLocation(request.location());
        travel.setStartDate(request.startDate());
        travel.setEndDate(request.endDate());
        if (request.visibility() != null) {
            travel.setVisibility(request.visibility());
        } else if (travel.getVisibility() == null) {
            travel.setVisibility(TravelVisibility.PRIVATE);
        }

        travel.setCategory(request.categoryId() != null
                ? entityManager.find(Category.class, request.categoryId())
                : null);
        travel.getTags().clear();
        if (request.tags() != null && !request.tags().isEmpty()) {
            travel.getTags().addAll(tagOperations.getTagsById(request.tags()));
        }

        if (request.coverImageId() != null) {
            travel.setCoverImage(fileOperations.getFileMetadata(request.coverImageId()));
        } else {
            travel.setCoverImage(null);
        }

        applyPhotos(travel, request);
        applyPlaces(travel, request);
        applyDayNotes(travel, request);
    }

    /**
     * Replace the travel's photos with those in the request, resolving each {@link CornerFile}
     * and carrying its optional location (EXIF GPS or manually placed). Clearing relies on
     * orphanRemoval to delete photos that were removed.
     */
    private void applyPhotos(Travel travel, TravelCreateRequest request) {
        travel.getPhotos().clear();
        if (request.photos() != null) {
            for (TravelPhotoRequest photoRequest : request.photos()) {
                if (photoRequest == null || photoRequest.fileId() == null) {
                    continue;
                }
                TravelPhoto photo = new TravelPhoto();
                photo.setTravel(travel);
                photo.setFile(fileOperations.getFileMetadata(photoRequest.fileId()));
                photo.setLatitude(photoRequest.latitude());
                photo.setLongitude(photoRequest.longitude());
                photo.setTakenOn(photoRequest.takenOn());
                photo.setNote(normalizeNote(photoRequest.note()));
                travel.getPhotos().add(photo);
            }
        }
    }

    /**
     * Replace the travel's visited places with those in the request.
     * Clearing the existing collection relies on orphanRemoval to delete the old rows.
     */
    private void applyPlaces(Travel travel, TravelCreateRequest request) {
        travel.getPlaces().clear();
        if (request.places() != null) {
            for (TravelPlaceRequest placeRequest : request.places()) {
                if (placeRequest == null || placeRequest.latitude() == null || placeRequest.longitude() == null) {
                    continue;
                }
                TravelPlace place = new TravelPlace();
                place.setTravel(travel);
                place.setName(placeRequest.name());
                place.setLatitude(placeRequest.latitude());
                place.setLongitude(placeRequest.longitude());
                travel.getPlaces().add(place);
            }
        }
    }

    /**
     * Replace the travel's per-day notes with those in the request. Entries without a day or
     * with a blank note are skipped, and only the last note wins per day so a (travel, day)
     * stays unique. Clearing the existing collection relies on orphanRemoval to delete old rows.
     */
    private void applyDayNotes(Travel travel, TravelCreateRequest request) {
        travel.getDayNotes().clear();
        if (request.dayNotes() == null) {
            return;
        }
        java.util.Map<java.time.LocalDate, String> byDay = new java.util.LinkedHashMap<>();
        for (TravelDayNoteRequest dayNoteRequest : request.dayNotes()) {
            if (dayNoteRequest == null || dayNoteRequest.day() == null) {
                continue;
            }
            String note = normalizeNote(dayNoteRequest.note());
            if (note == null) {
                byDay.remove(dayNoteRequest.day());
            } else {
                byDay.put(dayNoteRequest.day(), note);
            }
        }
        byDay.forEach((day, note) -> {
            TravelDayNote dayNote = new TravelDayNote();
            dayNote.setTravel(travel);
            dayNote.setDay(day);
            dayNote.setNote(note);
            travel.getDayNotes().add(dayNote);
        });
    }

    /** Trims a note and collapses blank/empty input to null so we don't store empty strings. */
    private String normalizeNote(String note) {
        if (note == null) {
            return null;
        }
        String trimmed = note.strip();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
