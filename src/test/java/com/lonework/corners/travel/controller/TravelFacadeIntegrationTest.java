package com.lonework.corners.travel.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.travel.model.Travel;
import com.lonework.corners.travel.model.TravelCreateRequest;
import com.lonework.corners.travel.model.TravelDetailResponse;
import com.lonework.corners.travel.model.TravelSummaryResponse;
import com.lonework.corners.travel.model.TravelVisibility;
import com.lonework.corners.travel.security.TravelSecurity;
import com.lonework.corners.travel.service.TravelService;
import com.lonework.corners.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TravelFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private TravelFacade travelFacade;

    @Autowired
    private TravelService travelService;

    @Autowired
    private TravelSecurity travelSecurity;

    @Test
    void createTravelGeneratesShareTokenAndLinksPhotos() {
        User owner = createUser("owner@example.com", "Owner");
        CornerFile photo = createCornerFile("photo.jpg", owner.getEmail());
        CornerFile cover = createCornerFile("cover.jpg", owner.getEmail());
        flushAndClear();

        TravelDetailResponse created = travelFacade.createTravel(
                request("Patagonia 2026", TravelVisibility.PRIVATE, cover.getId(), List.of(photo.getId())),
                owner.getEmail());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        assertNotNull(persisted);
        assertEquals("Patagonia 2026", persisted.getTitle());
        assertEquals(TravelVisibility.PRIVATE, persisted.getVisibility());
        assertNotNull(persisted.getShareToken());
        assertEquals(owner.getId(), persisted.getOwner().getId());
        assertEquals(1, persisted.getPhotos().size());
        assertEquals(cover.getId(), persisted.getCoverImage().getId());
        // The owner who creates the travel receives the share token in the response.
        assertNotNull(created.shareToken());
    }

    @Test
    void updateTravelChangesValuesAndVisibility() {
        User owner = createUser("update@example.com", "Updater");
        TravelDetailResponse created = travelFacade.createTravel(
                request("Draft", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        travelFacade.updateTravel(created.id(),
                request("Iceland Ring Road", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        assertEquals("Iceland Ring Road", persisted.getTitle());
        assertEquals(TravelVisibility.PUBLIC, persisted.getVisibility());
    }

    @Test
    void travelSecurityRecognisesOwnerOnly() {
        User owner = createUser("secowner@example.com", "SecOwner");
        createUser("intruder@example.com", "Intruder");
        TravelDetailResponse created = travelFacade.createTravel(
                request("Secret", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        assertTrue(travelSecurity.isOwner(created.id(), "secowner@example.com"));
        assertFalse(travelSecurity.isOwner(created.id(), "intruder@example.com"));
    }

    @Test
    void privateTravelIsHiddenFromOtherViewersButVisibleToOwner() {
        User owner = createUser("priv@example.com", "Priv");
        createUser("stranger@example.com", "Stranger");
        TravelDetailResponse created = travelFacade.createTravel(
                request("Private journal", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        assertNotNull(travelService.getTravelForViewer(created.id(), "priv@example.com"));
        assertThrows(EntityNotFoundException.class,
                () -> travelService.getTravelForViewer(created.id(), "stranger@example.com"));
    }

    @Test
    void shareTokenBypassesVisibility() {
        User owner = createUser("share@example.com", "Sharer");
        TravelDetailResponse created = travelFacade.createTravel(
                request("Shared trip", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        TravelDetailResponse shared = travelFacade.getSharedTravel(persisted.getShareToken());

        assertEquals(created.id(), shared.id());
        // The shared view never leaks the token to the visitor (it is already in their URL).
        assertNull(shared.shareToken());
    }

    @Test
    void followersVisibilityAllowsFollowersOnly() {
        User owner = createUser("followee@example.com", "Followee");
        User follower = createUser("follower@example.com", "Follower");
        User outsider = createUser("outsider@example.com", "Outsider");
        owner.getFollowers().add(follower);
        entityManager.merge(owner);
        TravelDetailResponse created = travelFacade.createTravel(
                request("Followers only", TravelVisibility.FOLLOWERS, null, List.of()), owner.getEmail());
        flushAndClear();

        assertNotNull(travelService.getTravelForViewer(created.id(), "follower@example.com"));
        assertThrows(EntityNotFoundException.class,
                () -> travelService.getTravelForViewer(created.id(), "outsider@example.com"));
    }

    @Test
    void publicListReturnsOnlyPublicTravels() {
        User owner = createUser("pub@example.com", "Publisher");
        travelFacade.createTravel(request("Public one", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        travelFacade.createTravel(request("Private one", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        List<TravelSummaryResponse> publicTravels = travelFacade.getPublicTravels(createPagingQueryParams()).data;

        assertTrue(publicTravels.stream().allMatch(t -> t.visibility() == TravelVisibility.PUBLIC));
        assertTrue(publicTravels.stream().anyMatch(t -> "Public one".equals(t.title())));
        assertFalse(publicTravels.stream().anyMatch(t -> "Private one".equals(t.title())));
    }

    @Test
    void travelsForViewerContainOnlyAccessibleTravels() {
        User owner = createUser("feedowner@example.com", "FeedOwner");
        User follower = createUser("feedfollower@example.com", "FeedFollower");
        createUser("feedstranger@example.com", "FeedStranger");
        owner.getFollowers().add(follower);
        entityManager.merge(owner);
        travelFacade.createTravel(request("Feed public", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        travelFacade.createTravel(request("Feed followers", TravelVisibility.FOLLOWERS, null, List.of()), owner.getEmail());
        travelFacade.createTravel(request("Feed private", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        List<String> ownerTitles = titles(travelFacade.getTravelsForViewer("feedowner@example.com"));
        assertTrue(ownerTitles.containsAll(List.of("Feed public", "Feed followers", "Feed private")));

        List<String> followerTitles = titles(travelFacade.getTravelsForViewer("feedfollower@example.com"));
        assertTrue(followerTitles.contains("Feed public"));
        assertTrue(followerTitles.contains("Feed followers"));
        assertFalse(followerTitles.contains("Feed private"));

        List<String> strangerTitles = titles(travelFacade.getTravelsForViewer("feedstranger@example.com"));
        assertTrue(strangerTitles.contains("Feed public"));
        assertFalse(strangerTitles.contains("Feed followers"));
        assertFalse(strangerTitles.contains("Feed private"));
    }

    @Test
    void travelsForAnonymousViewerContainOnlyPublicTravels() {
        User owner = createUser("anonfeed@example.com", "AnonFeed");
        travelFacade.createTravel(request("Anon public", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        travelFacade.createTravel(request("Anon private", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        List<String> anonymousTitles = titles(travelFacade.getTravelsForViewer(null));
        assertTrue(anonymousTitles.contains("Anon public"));
        assertFalse(anonymousTitles.contains("Anon private"));
    }

    @Test
    void userTravelsRespectViewerAccess() {
        User owner = createUser("profileowner@example.com", "ProfileOwner");
        User follower = createUser("profilefollower@example.com", "ProfileFollower");
        createUser("profilestranger@example.com", "ProfileStranger");
        owner.getFollowers().add(follower);
        entityManager.merge(owner);
        travelFacade.createTravel(request("Profile public", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        travelFacade.createTravel(request("Profile followers", TravelVisibility.FOLLOWERS, null, List.of()), owner.getEmail());
        travelFacade.createTravel(request("Profile private", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        List<String> ownTitles = titles(travelFacade.getUserTravels(owner.getId(), owner.getEmail()));
        assertTrue(ownTitles.containsAll(List.of("Profile public", "Profile followers", "Profile private")));

        List<String> followerTitles = titles(travelFacade.getUserTravels(owner.getId(), follower.getEmail()));
        assertTrue(followerTitles.containsAll(List.of("Profile public", "Profile followers")));
        assertFalse(followerTitles.contains("Profile private"));

        List<String> strangerTitles = titles(travelFacade.getUserTravels(owner.getId(), "profilestranger@example.com"));
        assertEquals(List.of("Profile public"), strangerTitles);

        List<String> anonymousTitles = titles(travelFacade.getUserTravels(owner.getId(), null));
        assertEquals(List.of("Profile public"), anonymousTitles);
    }

    @Test
    void anonymousViewerSeesOnlyPublicTravelDetail() {
        User owner = createUser("anondetail@example.com", "AnonDetail");
        TravelDetailResponse publicTravel = travelFacade.createTravel(
                request("Anon detail public", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        TravelDetailResponse privateTravel = travelFacade.createTravel(
                request("Anon detail private", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        TravelDetailResponse viewed = travelFacade.getTravel(publicTravel.id(), null);
        assertEquals(publicTravel.id(), viewed.id());
        // Anonymous viewers never receive the share token or a personal rating.
        assertNull(viewed.shareToken());
        assertNull(viewed.myRating());

        assertThrows(EntityNotFoundException.class, () -> travelFacade.getTravel(privateTravel.id(), null));
    }

    @Test
    void rateTravelAveragesRatingsAndUpsertsPerUser() {
        User owner = createUser("rateowner@example.com", "RateOwner");
        createUser("rater1@example.com", "RaterOne");
        createUser("rater2@example.com", "RaterTwo");
        TravelDetailResponse created = travelFacade.createTravel(
                request("Rated trip", TravelVisibility.PUBLIC, null, List.of()), owner.getEmail());
        flushAndClear();

        TravelDetailResponse afterFirst = travelFacade.rateTravel(created.id(), 5, "rater1@example.com");
        assertEquals(5.0, afterFirst.rating());
        assertEquals(5, afterFirst.myRating());

        TravelDetailResponse afterSecond = travelFacade.rateTravel(created.id(), 2, "rater2@example.com");
        assertEquals(3.5, afterSecond.rating());
        assertEquals(2, afterSecond.myRating());

        // Re-rating replaces the user's previous rating instead of adding another row.
        TravelDetailResponse afterRerate = travelFacade.rateTravel(created.id(), 4, "rater2@example.com");
        assertEquals(4.5, afterRerate.rating());
        assertEquals(4, afterRerate.myRating());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        assertEquals(4.5, persisted.getRating());
        // The viewer's own rating is echoed back when fetching the travel.
        assertEquals(4, travelFacade.getTravel(created.id(), "rater2@example.com").myRating());
    }

    @Test
    void rateTravelEnforcesVisibilityAndValidRange() {
        User owner = createUser("rateprivate@example.com", "RatePrivate");
        createUser("ratestranger@example.com", "RateStranger");
        TravelDetailResponse created = travelFacade.createTravel(
                request("Unrateable", TravelVisibility.PRIVATE, null, List.of()), owner.getEmail());
        flushAndClear();

        assertThrows(EntityNotFoundException.class,
                () -> travelFacade.rateTravel(created.id(), 4, "ratestranger@example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> travelFacade.rateTravel(created.id(), 0, owner.getEmail()));
        assertThrows(IllegalArgumentException.class,
                () -> travelFacade.rateTravel(created.id(), 6, owner.getEmail()));
    }

    @Test
    void createTravelPersistsCategoryAndTags() {
        User owner = createUser("cattag@example.com", "CatTag");
        Category category = createCategory("Hiking", CategoryType.TRAVEL);
        Tag quiet = createTag("quiet", "tester");
        Tag foodie = createTag("foodie", "tester");
        flushAndClear();

        TravelDetailResponse created = travelFacade.createTravel(
                new TravelCreateRequest(
                        "Tatry hike",
                        "A week in the mountains",
                        "Slovakia",
                        LocalDate.parse("2026-01-10"),
                        LocalDate.parse("2026-01-20"),
                        TravelVisibility.PUBLIC,
                        category.getId(),
                        List.of(quiet.getId(), foodie.getId()),
                        null,
                        List.of(),
                        List.of()),
                owner.getEmail());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        assertEquals(category.getId(), persisted.getCategory().getId());
        assertEquals(2, persisted.getTags().size());
        assertEquals(category.getId(), created.category().getId());
        assertEquals(2, created.tags().size());

        // Updating without category/tags clears them again.
        travelFacade.updateTravel(created.id(),
                new TravelCreateRequest(
                        "Tatry hike",
                        "A week in the mountains",
                        "Slovakia",
                        LocalDate.parse("2026-01-10"),
                        LocalDate.parse("2026-01-20"),
                        TravelVisibility.PUBLIC,
                        null,
                        List.of(),
                        null,
                        List.of(),
                        List.of()),
                owner.getEmail());
        flushAndClear();

        Travel cleared = entityManager.find(Travel.class, created.id());
        assertNull(cleared.getCategory());
        assertTrue(cleared.getTags().isEmpty());
    }

    @Test
    void createTravelPersistsVisitedPlaces() {
        User owner = createUser("places@example.com", "Places");
        flushAndClear();

        TravelDetailResponse created = travelFacade.createTravel(
                new TravelCreateRequest(
                        "Japan",
                        "Two weeks",
                        "Japan",
                        LocalDate.parse("2026-01-10"),
                        LocalDate.parse("2026-01-20"),
                        TravelVisibility.PRIVATE,
                        null,
                        null,
                        null,
                        List.of(),
                        List.of(
                                new com.lonework.corners.travel.model.TravelPlaceRequest("Osaka", 34.6937, 135.5023),
                                new com.lonework.corners.travel.model.TravelPlaceRequest("Tokyo", 35.6762, 139.6503))),
                owner.getEmail());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        assertEquals(2, persisted.getPlaces().size());
        assertEquals(2, created.places().size());
    }

    @Test
    void createTravelPersistsPhotoCoordinates() {
        User owner = createUser("geo@example.com", "Geo");
        CornerFile photo = createCornerFile("geo.jpg", owner.getEmail());
        flushAndClear();

        TravelDetailResponse created = travelFacade.createTravel(
                new TravelCreateRequest(
                        "Japan",
                        "Two weeks",
                        "Japan",
                        LocalDate.parse("2026-01-10"),
                        LocalDate.parse("2026-01-20"),
                        TravelVisibility.PRIVATE,
                        null,
                        null,
                        null,
                        List.of(new com.lonework.corners.travel.model.TravelPhotoRequest(
                                photo.getId(), 34.6937, 135.5023, java.time.LocalDate.parse("2026-01-12"))),
                        List.of()),
                owner.getEmail());
        flushAndClear();

        Travel persisted = entityManager.find(Travel.class, created.id());
        assertEquals(1, persisted.getPhotos().size());
        assertEquals(34.6937, persisted.getPhotos().getFirst().getLatitude());
        assertEquals(135.5023, persisted.getPhotos().getFirst().getLongitude());
        assertEquals(java.time.LocalDate.parse("2026-01-12"), persisted.getPhotos().getFirst().getTakenOn());
        assertEquals(34.6937, created.photos().getFirst().latitude());
    }

    private List<String> titles(List<TravelSummaryResponse> travels) {
        return travels.stream().map(TravelSummaryResponse::title).toList();
    }

    private TravelCreateRequest request(String title, TravelVisibility visibility, Long coverImageId, List<Long> fileIds) {
        return new TravelCreateRequest(
                title,
                title + " description",
                "Somewhere",
                LocalDate.parse("2026-01-10"),
                LocalDate.parse("2026-01-20"),
                visibility,
                null,
                null,
                coverImageId,
                fileIds.stream()
                        .map(id -> new com.lonework.corners.travel.model.TravelPhotoRequest(id, null, null, null))
                        .toList(),
                List.of()
        );
    }
}
