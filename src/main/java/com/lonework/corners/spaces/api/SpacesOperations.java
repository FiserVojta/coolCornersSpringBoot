package com.lonework.corners.spaces.api;

import java.io.InputStream;

public interface SpacesOperations {

    String uploadFile(String key, InputStream inputStream, long contentLength, String contentType);

    String uploadFile(String key, byte[] content, String contentType);

    InputStream downloadFile(String key);
}
