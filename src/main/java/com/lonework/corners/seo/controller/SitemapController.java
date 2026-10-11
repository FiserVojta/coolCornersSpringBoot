package com.lonework.corners.seo.controller;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/public")
public class SitemapController {

    private static final MediaType XML_UTF8 = new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8);

    @Autowired
    private SitemapFacade sitemapFacade;

    @GetMapping(path = "/sitemap.xml")
    @PermitAll
    public ResponseEntity<String> getSitemap() {
        return ResponseEntity.ok()
                .contentType(XML_UTF8)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(sitemapFacade.getSitemapXml());
    }
}
