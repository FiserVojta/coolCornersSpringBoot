package com.lonework.corners.wander.service;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.service.UserService;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderPart;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Configurable
@Transactional
public class WanderService {

    @Inject
    EntityManager entityManager;
    
    @Autowired
    UserService userService;

    public Wander createWander(WanderCreateRequest wanderCreateRequest, String createdBy) {
        // Create new Wander instance
        Wander wander = new Wander();
        
        // Set basic properties
        wander.setDescription(wanderCreateRequest.description());
        wander.setCapacity(wanderCreateRequest.capacity());
        wander.setStartTime(wanderCreateRequest.startTime());
        
        // Set the creator - fetch the actual user from database
        User creator = userService.getUser(createdBy);
        if (creator == null) {
            throw new EntityNotFoundException("User not found with email: " + createdBy);
        }
        wander.setCreatedBy(creator);
        
        // Set category if provided
        if (wanderCreateRequest.category() != null) {
            Category category = entityManager.find(Category.class, wanderCreateRequest.category());
            if (category == null) {
                throw new EntityNotFoundException("Category not found with id: " + wanderCreateRequest.category());
            }
            wander.setCategory(category);
        }
        
        // Set tags if provided
        if (wanderCreateRequest.tags() != null && !wanderCreateRequest.tags().isEmpty()) {
            List<Tag> tags = entityManager.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                    .setParameter("ids", wanderCreateRequest.tags())
                    .getResultList();
            
            if (tags.size() != wanderCreateRequest.tags().size()) {
                throw new EntityNotFoundException("Some tags were not found");
            }
            wander.setTags(tags);
        } else {
            wander.setTags(new ArrayList<>());
        }
        
        // Set wanderers (participants) if provided
        if (wanderCreateRequest.wanderers() != null && !wanderCreateRequest.wanderers().isEmpty()) {
            List<User> wanderers = entityManager.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                    .setParameter("ids", wanderCreateRequest.wanderers())
                    .getResultList();
            
            if (wanderers.size() != wanderCreateRequest.wanderers().size()) {
                throw new EntityNotFoundException("Some wanderers were not found");
            }
            wander.setWanderers(wanderers);
        } else {
            wander.setWanderers(new ArrayList<>());
        }
        
        // Initialize empty list for wander parts
        wander.setWanderParts(new ArrayList<>());
        
        // Persist the wander entity FIRST before creating wander parts
        entityManager.persist(wander);
        entityManager.flush(); // Ensure the entity is saved to database
        
        // Now create and set wander parts if provided
        if (wanderCreateRequest.wanderParts() != null && !wanderCreateRequest.wanderParts().isEmpty()) {
            List<WanderPart> wanderParts = new ArrayList<>();
            
            for (WanderCreateRequest.WanderPartCreateRequest partRequest : wanderCreateRequest.wanderParts()) {
                WanderPart wanderPart = new WanderPart();
                wanderPart.setOrder(partRequest.order());
                
                // Set places for this wander part
                if (partRequest.places() != null && !partRequest.places().isEmpty()) {
                    List<Place> places = entityManager.createQuery("SELECT p FROM Place p WHERE p.id IN :ids", Place.class)
                            .setParameter("ids", partRequest.places())
                            .getResultList();
                    
                    if (places.size() != partRequest.places().size()) {
                        throw new EntityNotFoundException("Some places were not found for wander part");
                    }
                    wanderPart.setPlaces(places);
                } else {
                    wanderPart.setPlaces(new ArrayList<>());
                }
                
                // Set trips for this wander part
                if (partRequest.trips() != null && !partRequest.trips().isEmpty()) {
                    List<Trip> trips = entityManager.createQuery("SELECT t FROM Trip t WHERE t.id IN :ids", Trip.class)
                            .setParameter("ids", partRequest.trips())
                            .getResultList();
                    
                    if (trips.size() != partRequest.trips().size()) {
                        throw new EntityNotFoundException("Some trips were not found for wander part");
                    }
                    wanderPart.setTrips(trips);
                } else {
                    wanderPart.setTrips(new ArrayList<>());
                }
                
                // Set the wander reference - now wander is already persisted
                wanderPart.setWander(wander);
                
                // Persist the wander part
                entityManager.persist(wanderPart);
                wanderParts.add(wanderPart);
            }
            
            // Update the wander with its parts
            wander.setWanderParts(wanderParts);
            entityManager.merge(wander);
        }
        
        return wander;
    }

    public List<Wander> getAllWanders() {
        return entityManager.createQuery("SELECT w FROM Wander w", Wander.class).getResultList();
    }

    public Wander getWander(Long id) {
        return entityManager.createQuery("SELECT w FROM Wander w where w.id = :id", Wander.class)
                .setParameter("id", id)
                .getSingleResult();
    }
    
    /**
     * Update an existing wander with new data
     */
    @Transactional
    public Wander updateWander(Long wanderId, WanderCreateRequest wanderCreateRequest, String updatedBy) {
        Wander existingWander = entityManager.find(Wander.class, wanderId);
        if (existingWander == null) {
            throw new EntityNotFoundException("Wander not found with id: " + wanderId);
        }
        
        // Update basic properties
        existingWander.setDescription(wanderCreateRequest.description());
        existingWander.setCapacity(wanderCreateRequest.capacity());
        existingWander.setStartTime(wanderCreateRequest.startTime());
        
        // Update category if provided
        if (wanderCreateRequest.category() != null) {
            Category category = entityManager.find(Category.class, wanderCreateRequest.category());
            if (category == null) {
                throw new EntityNotFoundException("Category not found with id: " + wanderCreateRequest.category());
            }
            existingWander.setCategory(category);
        }
        
        // Update tags
        if (wanderCreateRequest.tags() != null) {
            List<Tag> tags = entityManager.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                    .setParameter("ids", wanderCreateRequest.tags())
                    .getResultList();
            existingWander.setTags(tags);
        }
        
        // Update wanderers
        if (wanderCreateRequest.wanderers() != null) {
            List<User> wanderers = entityManager.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                    .setParameter("ids", wanderCreateRequest.wanderers())
                    .getResultList();
            existingWander.setWanderers(wanderers);
        }
        
        // Update wander parts - remove old ones and add new ones
        // First, remove existing wander parts
        if (existingWander.getWanderParts() != null) {
            for (WanderPart oldPart : existingWander.getWanderParts()) {
                entityManager.remove(oldPart);
            }
        }
        
        // Create new wander parts
        if (wanderCreateRequest.wanderParts() != null && !wanderCreateRequest.wanderParts().isEmpty()) {
            List<WanderPart> newWanderParts = new ArrayList<>();
            
            for (WanderCreateRequest.WanderPartCreateRequest partRequest : wanderCreateRequest.wanderParts()) {
                WanderPart wanderPart = new WanderPart();
                wanderPart.setOrder(partRequest.order());
                wanderPart.setWander(existingWander);
                
                // Set places
                if (partRequest.places() != null && !partRequest.places().isEmpty()) {
                    List<Place> places = entityManager.createQuery("SELECT p FROM Place p WHERE p.id IN :ids", Place.class)
                            .setParameter("ids", partRequest.places())
                            .getResultList();
                    wanderPart.setPlaces(places);
                }
                
                // Set trips
                if (partRequest.trips() != null && !partRequest.trips().isEmpty()) {
                    List<Trip> trips = entityManager.createQuery("SELECT t FROM Trip t WHERE t.id IN :ids", Trip.class)
                            .setParameter("ids", partRequest.trips())
                            .getResultList();
                    wanderPart.setTrips(trips);
                }
                
                entityManager.persist(wanderPart);
                newWanderParts.add(wanderPart);
            }
            
            existingWander.setWanderParts(newWanderParts);
        } else {
            existingWander.setWanderParts(new ArrayList<>());
        }
        
        return entityManager.merge(existingWander);
    }
    
    /**
     * Delete a wander
     */
    @Transactional
    public void deleteWander(Long wanderId) {
        Wander wander = entityManager.find(Wander.class, wanderId);
        if (wander == null) {
            throw new EntityNotFoundException("Wander not found with id: " + wanderId);
        }
        
        // Remove wander parts first
        if (wander.getWanderParts() != null) {
            for (WanderPart part : wander.getWanderParts()) {
                entityManager.remove(part);
            }
        }
        
        entityManager.remove(wander);
    }
}
