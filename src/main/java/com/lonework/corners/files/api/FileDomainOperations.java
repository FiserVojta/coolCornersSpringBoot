package com.lonework.corners.files.api;

import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerFileList;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileDomainOperations implements FileOperations {

    private final EntityManager entityManager;

    public FileDomainOperations(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CornerFile getFileMetadata(Long fileId) {
        return entityManager.find(CornerFile.class, fileId);
    }

    @Override
    public List<CornerFileList> getCornerFilesList(List<CornerFile> files) {
        return files.stream().map(CornerFileList::new).toList();
    }
}
