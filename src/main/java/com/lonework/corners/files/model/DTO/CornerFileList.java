package com.lonework.corners.files.model.DTO;

import com.lonework.corners.files.model.CornerFile;


public record CornerFileList(
        Long id,
        String url,
        String name
) {
    public CornerFileList(CornerFile file){
        this(file.getId(), file.getUrl(), file.getUrl());
    }
}
