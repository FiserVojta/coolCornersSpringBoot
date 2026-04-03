package com.lonework.corners.trip.services;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.api.CommentOperations;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.files.api.FileOperations;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.place.api.PlaceOperations;
import com.lonework.corners.place.model.DTO.PlaceListRequest;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.place.model.PlaceSimpleResponse;
import com.lonework.corners.tag.api.TagOperations;
import com.lonework.corners.trip.model.DTO.TripCommentRequest;
import com.lonework.corners.trip.model.DTO.TripCreateRequest;
import com.lonework.corners.trip.model.DTO.TripDetailResponse;
import com.lonework.corners.trip.model.DTO.TripRateRequest;
import com.lonework.corners.trip.model.DTO.TripUpdateRequest;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.TripRating;
import com.lonework.corners.trip.model.TripSearchRequest;
import com.lonework.corners.user.api.UserOperations;
import com.lonework.corners.user.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Configurable
@Transactional
public class TripService {

    private final EntityManager entityManager;
    private final CommentOperations commentOperations;
    private final PlaceOperations placeOperations;
    private final TagOperations tagOperations;
    private final FileOperations fileOperations;
    private final UserOperations userOperations;

    public TripService(
            EntityManager entityManager,
            CommentOperations commentOperations,
            PlaceOperations placeOperations,
            TagOperations tagOperations,
            FileOperations fileOperations,
            UserOperations userOperations
    ) {
        this.entityManager = entityManager;
        this.commentOperations = commentOperations;
        this.placeOperations = placeOperations;
        this.tagOperations = tagOperations;
        this.fileOperations = fileOperations;
        this.userOperations = userOperations;
    }

    public Trip createTrip(TripCreateRequest tripCreateRequest, String createdBy) {
        Trip trip = new Trip(tripCreateRequest, createdBy);
        trip.setCategory(entityManager.find(Category.class, tripCreateRequest.getCategoryId()));
        trip.setTags(tripCreateRequest.getTags().stream()
                .map(id -> entityManager.find(com.lonework.corners.tag.model.Tag.class, id))
                .toList());
        if (tripCreateRequest.getPlaceIds() != null && !tripCreateRequest.getPlaceIds().isEmpty()) {
            trip.setPlaces(tripCreateRequest.getPlaceIds().stream()
                    .map(placeOperations::getPlaceById)
                    .toList());
        }
        if(tripCreateRequest.getGooglePlaces() != null && !tripCreateRequest.getGooglePlaces().isEmpty()) {
            trip.setGooglePlaces(placeOperations.getOrCreateGooglePlaces(tripCreateRequest.getGooglePlaces()));
        }
        if(tripCreateRequest.getFiles() != null && !tripCreateRequest.getFiles().isEmpty()) {
            List<CornerFile> files = new ArrayList<>();
            for (var file : tripCreateRequest.getFiles()) {
                files.add(fileOperations.getFileMetadata(file.fileId()));
            }
            trip.setCornerFiles(files);
        }

        return entityManager.merge(trip);
    }

    public TripDetailResponse findTripById(long id) {
        return getTripDetailResponse(entityManager.find(Trip.class, id));
    }

    public TripDetailResponse markTripDone(Long tripId, String userEmail) {
        Trip trip = entityManager.find(Trip.class, tripId);
        if (trip == null) {
            throw new EntityNotFoundException("Trip not found");
        }

        User user = userOperations.getRequiredUserByEmail(userEmail);

        if (trip.getCompletedByUsers() == null) {
            trip.setCompletedByUsers(new ArrayList<>());
        }

        if (trip.getCompletedByUsers().stream().noneMatch(existingUser -> existingUser.getId().equals(user.getId()))) {
            trip.getCompletedByUsers().add(user);
            entityManager.merge(trip);
        }

        return getTripDetailResponse(trip);
    }

    public Optional<Trip> addPlacesToTrip(PlaceListRequest placeListRequest, Long tripId) {
        Trip trip = entityManager.find(Trip.class, tripId);
        if (trip == null) {
            return Optional.empty();
        }

        List<Long> placeIds = placeListRequest.getPlaceIds();
        if (placeIds == null || placeIds.isEmpty()) {
            return Optional.of(trip);
        }

        List<Place> updatedPlaces = new ArrayList<>();
        if (trip.getPlaces() != null) {
            updatedPlaces.addAll(trip.getPlaces());
        }

        List<Place> placesToAdd = placeOperations.findPlacesByIds(placeIds);
        for (Place place : placesToAdd) {
            boolean alreadyLinked = updatedPlaces.stream()
                    .anyMatch(existingPlace -> existingPlace.getId().equals(place.getId()));
            if (!alreadyLinked) {
                updatedPlaces.add(place);
            }
        }

        trip.setPlaces(updatedPlaces);
        return Optional.of(entityManager.merge(trip));
    }

    public PagedResult<Trip> findTripByParameters(TripSearchRequest tripSearchRequest, PagingQueryParams pagingQueryParams) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Build predicates list
        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        // Data query
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> tripRoot = cq.from(Trip.class);

        if (tripSearchRequest.categories() != null && !tripSearchRequest.categories().isEmpty()) {
            predicates.add(tripRoot.get("category").get("id").in(tripSearchRequest.categories()));
        }
        if (tripSearchRequest.tags() != null && !tripSearchRequest.tags().isEmpty()) {
            predicates.add(tripRoot.get("tags").get("id").in(tripSearchRequest.tags()));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
        }
        var page = pagingQueryParams.page() != null ? pagingQueryParams.page() : 0;
        var size =pagingQueryParams.size() != null ? pagingQueryParams.size() : 10;
        var data = entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        // Count query with same predicates
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Trip> countRoot = countQuery.from(Trip.class);
        countQuery.select(cb.count(countRoot));

        List<jakarta.persistence.criteria.Predicate> countPredicates = new ArrayList<>();
        if (tripSearchRequest.categories() != null && !tripSearchRequest.categories().isEmpty()) {
            countPredicates.add(countRoot.get("category").get("id").in(tripSearchRequest.categories()));
        }
        if (tripSearchRequest.tags() != null && !tripSearchRequest.tags().isEmpty()) {
            countPredicates.add(countRoot.get("tags").get("id").in(tripSearchRequest.tags()));
        }

        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
        }

        long total = entityManager.createQuery(countQuery).getSingleResult();
        return new PagedResult<>(data, total);
    }

    @Transactional
    public Double rateTrip(TripRateRequest tripRateRequest, Long tripId) {
        var trip = entityManager.find(Trip.class, tripId);
        if (trip == null) {
            throw new EntityNotFoundException("Trip not found");
        }
        entityManager.merge(new TripRating(tripRateRequest, tripId));
        trip.setRating(countTripRating(tripId));
        entityManager.merge(trip);

        return trip.getRating();
    }


    public void commentTrip(TripCommentRequest tripCommentRequest, Long tripId, String createdBy) {
        var trip = entityManager.find(Trip.class, tripId);
        if (trip == null) {
            throw new EntityNotFoundException("Trip not found");
        }
        var comment = new Comment(tripCommentRequest, trip, createdBy);
        commentOperations.createComment(comment);
    }

    @Transactional
    public void updateTrip(TripUpdateRequest tripUpdateRequest, Long tripId, String createdBy) {
        var trip = entityManager.find(Trip.class, tripId);
        if (trip == null) {
            throw new EntityNotFoundException("Trip not found");
        }
        trip.setCategory(entityManager.find(Category.class, tripUpdateRequest.categoryId()));
        trip.setDescription(tripUpdateRequest.description());
        trip.setGooglePlaces(placeOperations.getOrCreateGooglePlaces(tripUpdateRequest.googlePlaces()));
        trip.setTags(tagOperations.getTagsById(tripUpdateRequest.tags()));
        if(tripUpdateRequest.files() != null && !tripUpdateRequest.files().isEmpty()) {
            List<CornerFile> files = new ArrayList<>();
            for (var file : tripUpdateRequest.files()) {
                files.add(fileOperations.getFileMetadata(file.fileId()));
            }
            trip.setCornerFiles(files);
        }
        if(tripUpdateRequest.backgroundImage() != null) {
            trip.setBackgroundImage(fileOperations.getFileMetadata(tripUpdateRequest.backgroundImage().fileId()));
        }
        entityManager.merge(trip);
    }

    private Double countTripRating(Long tripId) {
        return entityManager
                .createQuery("SELECT AVG(tr.rating) FROM TripRating tr WHERE tr.tripId = :tripId", Double.class)
                .setParameter("tripId", tripId)
                .getSingleResult();
    }

    private TripDetailResponse getTripDetailResponse(Trip trip) {
        return new TripDetailResponse(
                trip,
                getPlacesDetailResponse(trip.getPlaces()),
                null,
                fileOperations.getCornerFilesList(trip.getCornerFiles()));
    }

    private List<PlaceSimpleResponse> getPlacesDetailResponse(List<Place> places) {
        return places.stream()
                .map(this::getSimplePlaceResponse)
                .toList();
    }

    private PlaceSimpleResponse getSimplePlaceResponse(Place place) {
        return new PlaceSimpleResponse(
                place.getId(),
                place.getName(),
                place.getImage(),
                null,
                placeOperations.getPlaceFeature(place)
        );
    }

}
