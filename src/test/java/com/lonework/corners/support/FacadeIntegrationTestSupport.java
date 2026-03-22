package com.lonework.corners.support;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.common.model.QueryOrder;
import com.lonework.corners.common.model.ResultOrder;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.location.model.City;
import com.lonework.corners.location.model.Country;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.user.model.User;
import jakarta.persistence.EntityManager;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public abstract class FacadeIntegrationTestSupport extends PostgresIntegrationTest {

    @Autowired
    protected EntityManager entityManager;

    protected Category createCategory(String name, CategoryType categoryType) {
        Category category = new Category();
        category.setId(nextId("Category", "c"));
        category.setName(name);
        category.setTitle(name);
        category.setMain(true);
        category.setCategoryType(categoryType);
        entityManager.persist(category);
        return category;
    }

    protected Country createCountry(String name, String code) {
        Country country = new Country();
        country.setId(nextId("Country", "c"));
        country.setName(name);
        country.setCode(code);
        entityManager.persist(country);
        return country;
    }

    protected City createCity(String name, Country country) {
        City city = new City();
        city.setId(nextId("City", "c"));
        city.setName(name);
        city.setCountry(country);
        entityManager.persist(city);
        return city;
    }

    protected Tag createTag(String name, String creator) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreator(creator);
        entityManager.persist(tag);
        return tag;
    }

    protected User createUser(String email, String name) {
        User user = new User();
        user.setKeycloakId("kc-" + email);
        user.setEmail(email);
        user.setName(name);
        user.setDisplayName(name);
        user.setCreatedAt(ZonedDateTime.parse("2026-03-22T10:00:00Z"));
        entityManager.persist(user);
        return user;
    }

    protected CornerFile createCornerFile(String name, String createdBy) {
        CornerFile file = new CornerFile();
        file.setName(name);
        file.setUrl("https://files.example/" + name);
        file.setCreatedBy(createdBy);
        file.setEntityStatus(EntityStatus.ACTIVE);
        file.setCreatedAt(ZonedDateTime.parse("2026-03-22T10:00:00Z"));
        entityManager.persist(file);
        return file;
    }

    protected Place createPlace(String name, Category category, List<Tag> tags, String createdBy) {
        Place place = new Place();
        place.setName(name);
        place.setDescription(name + " description");
        place.setPhoneNumber("+420111222333");
        place.setPrice(19.5);
        place.setOpeningHours("09:00-18:00");
        place.setImage(name + "-image");
        place.setCreatedBy(createdBy);
        place.setCategory(category);
        place.setTags(new ArrayList<>(tags));
        place.setComments(new ArrayList<>());
        place.setGeometry(createPoint(14.42, 50.08));
        entityManager.persist(place);
        return place;
    }

    protected Trip createTrip(String name, Category category, List<Tag> tags, List<Place> places, String createdBy) {
        Trip trip = new Trip();
        trip.setName(name);
        trip.setDescription(name + " description");
        trip.setDuration(120);
        trip.setCategory(category);
        trip.setTags(new ArrayList<>(tags));
        trip.setPlaces(new ArrayList<>(places));
        trip.setGooglePlaces(new ArrayList<>());
        trip.setCornerFiles(new ArrayList<>());
        trip.setComments(new ArrayList<>());
        trip.setCreator(createdBy);
        trip.setCreatedBy(createdBy);
        trip.setGeometry(createPoint(14.5, 50.1));
        entityManager.persist(trip);
        return trip;
    }

    protected Point createPoint(double x, double y) {
        Point point = new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(x, y)}), new org.locationtech.jts.geom.GeometryFactory(new PrecisionModel(), 4326));
        point.setSRID(4326);
        return point;
    }

    protected PagingQueryParams createPagingQueryParams() {
        return new PagingQueryParams(0, 20, createResultOrder(), QueryOrder.ASC);
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Long nextId(String entityName, String alias) {
        Long maxId = entityManager.createQuery(
                        "SELECT COALESCE(MAX(" + alias + ".id), 0) FROM " + entityName + " " + alias,
                        Long.class
                )
                .getSingleResult();
        return maxId + 1;
    }

    private ResultOrder createResultOrder() {
        ResultOrder resultOrder = new ResultOrder();
        resultOrder.setOrderBy("createdAt");
        resultOrder.setOrderDirection("asc");
        return resultOrder;
    }
}
