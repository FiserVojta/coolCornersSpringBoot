package com.lonework.corners.seo.controller;

import com.lonework.corners.seo.model.SitemapEntry;
import com.lonework.corners.seo.service.SitemapService;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SitemapFacade {

    @Inject
    SitemapService sitemapService;

    @Value("${app.public-base-url:https://coolcorners.org}")
    String publicBaseUrl;

    public String getSitemapXml() {
        String baseUrl = normalizeBaseUrl(publicBaseUrl);
        List<SitemapEntry> entries = sitemapService.getSitemapEntries();

        StringBuilder xml = new StringBuilder(128 + entries.size() * 96);
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (SitemapEntry entry : entries) {
            xml.append("<url><loc>").append(escapeXml(baseUrl + entry.path())).append("</loc>");
            if (entry.lastModified() != null) {
                xml.append("<lastmod>").append(entry.lastModified()).append("</lastmod>");
            }
            xml.append("</url>\n");
        }
        xml.append("</urlset>\n");
        return xml.toString();
    }

    private static String normalizeBaseUrl(String baseUrl) {
        String trimmed = baseUrl == null ? "" : baseUrl.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    private static String escapeXml(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            switch (c) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&apos;");
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
