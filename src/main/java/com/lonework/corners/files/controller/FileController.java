package com.lonework.corners.files.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    FileFacade fileFacade;

    @PostMapping("")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        var response = fileFacade.uploadFile(file, jwt.getClaimAsString("email"));
        return ResponseEntity.ok(response);
    }
}