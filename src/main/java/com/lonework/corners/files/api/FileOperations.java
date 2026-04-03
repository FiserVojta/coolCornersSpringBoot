package com.lonework.corners.files.api;

import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerFileList;

import java.util.List;

public interface FileOperations {

    CornerFile getFileMetadata(Long fileId);

    List<CornerFileList> getCornerFilesList(List<CornerFile> files);
}
