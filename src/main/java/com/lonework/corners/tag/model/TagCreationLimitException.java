package com.lonework.corners.tag.model;

/**
 * Thrown when a user exceeds the number of new tags they may create within the rate-limit window.
 */
public class TagCreationLimitException extends RuntimeException {

    public TagCreationLimitException(String message) {
        super(message);
    }
}
