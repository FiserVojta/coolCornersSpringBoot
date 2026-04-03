package com.lonework.corners.tag.api;

import com.lonework.corners.tag.model.Tag;

import java.util.List;

public interface TagOperations {

    List<Tag> getTagsById(List<Long> ids);
}
