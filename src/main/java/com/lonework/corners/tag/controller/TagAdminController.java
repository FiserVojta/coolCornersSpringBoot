package com.lonework.corners.tag.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.tag.model.TagAdminResponse;
import com.lonework.corners.tag.model.TagMergeRequest;
import com.lonework.corners.tag.model.TagStatus;
import com.lonework.corners.tag.model.TagStatusUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only tag moderation endpoints: listing with usage, hide/unhide, and merge.
 */
@RestController
@RequestMapping("/admin/tags")
public class TagAdminController {

    @Autowired
    private TagFacade tagFacade;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResult<TagAdminResponse> getTags(@RequestParam(name = "q", required = false) String q,
                                                 @RequestParam(name = "status", required = false) TagStatus status,
                                                 @RequestParam(name = "page", required = false) Integer page,
                                                 @RequestParam(name = "size", required = false) Integer size) {
        return tagFacade.getTagsForAdmin(q, status, new PagingQueryParams(page, size, null, null));
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public TagAdminResponse updateTagStatus(@PathVariable("id") Long id, @RequestBody TagStatusUpdateRequest request) {
        return tagFacade.setTagStatus(id, request == null ? null : request.status());
    }

    @PostMapping(path = "/{id}/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public TagAdminResponse mergeTag(@PathVariable("id") Long id, @RequestBody TagMergeRequest request) {
        return tagFacade.mergeTags(id, request == null ? null : request.targetId());
    }
}
