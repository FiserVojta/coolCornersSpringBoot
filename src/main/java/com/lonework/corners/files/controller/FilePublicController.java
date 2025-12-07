package com.lonework.corners.files.controller;


import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.files.model.DTO.CornerListFile;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/public/files")
public class FilePublicController {

    @Autowired
    FileFacade fileFacade;

    @GetMapping(path = "/list")
    @PermitAll
    public ResponseEntity<PagedResult<CornerListFile>> listFiles() {
        return ResponseEntity.ok()
                .body(fileFacade.getFiles());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id) throws IOException {
        byte[] imageBytes = fileFacade.getFile(id).readAllBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}