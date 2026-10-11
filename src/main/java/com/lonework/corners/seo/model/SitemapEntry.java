package com.lonework.corners.seo.model;

import java.time.LocalDate;

/**
 * A single sitemap URL, relative to the public site root, with an optional last-modified date.
 */
public record SitemapEntry(String path, LocalDate lastModified) {
}
