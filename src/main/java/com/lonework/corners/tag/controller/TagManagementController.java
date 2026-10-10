package com.lonework.corners.tag.controller;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authenticated tag endpoints. Read-only tag endpoints stay public in {@link TagController}.
 */
@RestController
@RequestMapping("/tags")
public class TagManagementController {

    @Autowired
    private TagFacade tagFacade;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
    public Tag createTag(@RequestBody TagCreateRequest tagCreateRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return tagFacade.createTag(tagCreateRequest, email);
        }
        throw new RuntimeException("JWT token not found or invalid");
    }
}
