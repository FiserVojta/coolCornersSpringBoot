package com.lonework.corners.files.controller;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerFileList;
import com.lonework.corners.spaces.api.SpacesOperations;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@ApplicationScope
@Service
public class FileFacade {


    @Inject
    EntityManager entityManager;

    @Inject
    SpacesOperations spacesOperations;

    @Transactional
    public CornerFile uploadFile(MultipartFile file, String createdBy) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String fileUrl = spacesOperations.uploadFile(
                    fileName,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );
            var cornerFile = new CornerFile();
            cornerFile.setCreatedAt(ZonedDateTime.now());
            cornerFile.setCreatedBy(createdBy);
            cornerFile.setName(fileName);
            cornerFile.setUrl(fileUrl);
            cornerFile.setName(fileName);
            cornerFile.setEntityStatus(EntityStatus.ACTIVE);
            entityManager.persist(cornerFile);
            return cornerFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public InputStream getFile(Long fileId) {
        var url = entityManager.find(CornerFile.class, fileId).getName();
        return spacesOperations.downloadFile(url);
    }

    public CornerFile getFileMetadata(Long fileId) {
        return entityManager.find(CornerFile.class, fileId);
    }

    public PagedResult<CornerFileList> getFiles() {
        var files = entityManager.createQuery("select c.id, c.url, c.name from CornerFile c", CornerFileList.class).getResultList();
        return new PagedResult<>(files, 1L);
    }

    public List<CornerFileList> getCornerFilesList(List<CornerFile> files) {
        return files.stream().map(CornerFileList::new).toList();
    }
}
