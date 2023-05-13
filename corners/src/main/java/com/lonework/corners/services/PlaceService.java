package com.lonework.corners.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.repository.CommentRepository;
import com.lonework.corners.repository.PlaceRepository;
import com.lonework.corners.model.Comment;
import com.lonework.corners.model.Place;
import com.lonework.corners.model.request.CommentCreateRequest;
import com.lonework.corners.model.request.PlaceSearchRequest;

@Service
@Configurable
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Place getPlaceById(Long id) {
        // Place place = new Place("1", "Milohlídka", "Rozhledna na cerovce", 2,
        // "123456789", 200,
        // "08:00-17:00", null, null, "img", null, "Jicin", "what", null);

        Optional<Place> placeOptional = this.placeRepository.findById(id);
        if (placeOptional.isPresent()) {
            return placeOptional.get();
        } else {
            return null;
        }

    }

    public Iterable<Place> findPlacesByParametrs(PlaceSearchRequest placeSearchRequest) {

        return placeRepository.findRandomByAtributes(placeSearchRequest.getCity_id());

    }

    public Place createPlace(Place place) {
        return this.placeRepository.save(place);

    }

    public Comment createComment(CommentCreateRequest request, long placeId) {

        Comment comment = new Comment(request);
        Place place = this.placeRepository.findById(placeId).get();
        comment.setPlace(place);

        return this.commentRepository.save(comment);
    }
}
