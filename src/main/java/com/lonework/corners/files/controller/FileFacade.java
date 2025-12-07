package com.lonework.corners.files.controller;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerListFile;
import com.lonework.corners.spaces.controller.SpacesFacade;
import com.lonework.corners.spaces.services.DigitalOceanSpacesService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.UUID;


@ApplicationScope
@Service
public class FileFacade {


    @Inject
    EntityManager entityManager;

    @Inject
    SpacesFacade spacesFacade;

    @Transactional
    public void uploadFile(MultipartFile file, String createdBy) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String fileUrl = spacesFacade.uploadFile(
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
        } catch (IOException e) {

        }
    }


    public InputStream getFile(Long fileId) {
        var url = entityManager.find(CornerFile.class, fileId).getName();
        return spacesFacade.downloadFile(url);
    }

    public PagedResult<CornerListFile> getFiles() {
        var files = entityManager.createQuery("select c.id, c.url from CornerFile c", CornerListFile.class).getResultList();
        return new PagedResult<>(files, 1L);
    }
}
