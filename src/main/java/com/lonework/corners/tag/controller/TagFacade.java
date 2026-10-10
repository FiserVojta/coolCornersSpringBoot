package com.lonework.corners.tag.controller;


import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagAdminResponse;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagSearchRequest;
import com.lonework.corners.tag.model.TagStatus;
import com.lonework.corners.tag.model.TagSuggestion;
import com.lonework.corners.tag.services.TagService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;


@Service
public class TagFacade {

    @Autowired
    TagService tagService;

    @Autowired
    EntityManager entityManager;

    public Tag getTagById(Long id) {
        return entityManager.find(Tag.class, id);
    }

    public List<Tag> getTagsById(List<Long> id) {
        return entityManager.createQuery("select t from Tag t where t.id in (:ids)", Tag.class)
                .setParameter("ids", id)
                .getResultList();
    }

    public List<Tag> getTagsWithRequest(TagSearchRequest tagSearchRequest) {
        return tagService.getTagsWithRequest(tagSearchRequest);
    }

    public Tag createTag(TagCreateRequest tagCreateRequest, String creator) {
        return tagService.createTag(tagCreateRequest, creator);
    }

    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    public List<TagSuggestion> searchTags(String q, Integer limit) {
        return tagService.searchTags(q, limit);
    }

    public List<Tag> getTagsByNormalizedNames(List<String> names) {
        return tagService.getTagsByNormalizedNames(names);
    }

    public PagedResult<TagAdminResponse> getTagsForAdmin(String q, TagStatus status, PagingQueryParams paging) {
        return tagService.getTagsForAdmin(q, status, paging);
    }

    public TagAdminResponse setTagStatus(Long id, TagStatus status) {
        return tagService.setTagStatus(id, status);
    }

    public TagAdminResponse mergeTags(Long sourceId, Long targetId) {
        return tagService.mergeTags(sourceId, targetId);
    }

    public int deleteUnusedUserTags(Duration minAge) {
        return tagService.deleteUnusedUserTags(minAge);
    }

}
