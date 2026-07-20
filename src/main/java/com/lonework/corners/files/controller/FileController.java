package com.lonework.corners.files.controller;


import com.lonework.corners.files.model.CornerFile;
import com.lonework.corners.files.model.DTO.CornerFileCompleteRequest;
import com.lonework.corners.files.model.DTO.CornerFilePresignRequest;
import com.lonework.corners.files.model.DTO.CornerFilePresignResponse;
import com.lonework.corners.spaces.services.DigitalOceanSpacesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    FileFacade fileFacade;

    @Autowired
    S3Client s3Client;

    @Autowired
    DigitalOceanSpacesProperties spacesProperties;

    @PostMapping("")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        var response = fileFacade.uploadFile(file, jwt.getClaimAsString("email"));
        return ResponseEntity.ok(response);
    }

    /** Step 1 of the direct-to-storage upload: hand the browser presigned PUT URLs. */
    @PostMapping("/presign")
    public ResponseEntity<CornerFilePresignResponse> presignUpload(@RequestBody CornerFilePresignRequest request) {
        return ResponseEntity.ok(fileFacade.presignUpload(request));
    }

    /** Step 2: after the browser PUT the object(s) to storage, record the file. */
    @PostMapping("/complete")
    public ResponseEntity<CornerFile> completeUpload(
            @RequestBody CornerFileCompleteRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(fileFacade.completeUpload(request, jwt.getClaimAsString("email")));
    }

    // TEMPORARY: diagnose DO Spaces uploads. Delete once the prod issue is resolved.
    @GetMapping("/debug/spaces")
    public ResponseEntity<Map<String, Object>> debugSpaces() {
        Map<String, Object> out = new LinkedHashMap<>();

        String accessKey = spacesProperties.getAccessKey();
        String secretKey = spacesProperties.getSecretKey();
        out.put("accessKeyPrefix", accessKey != null && accessKey.length() >= 4 ? accessKey.substring(0, 4) : "n/a");
        out.put("accessKeyLength", accessKey == null ? 0 : accessKey.length());
        out.put("secretKeyLength", secretKey == null ? 0 : secretKey.length());
        out.put("bucket", spacesProperties.getBucket());
        out.put("region", spacesProperties.getRegion());
        out.put("endpoint", spacesProperties.getEndpoint());

        String key = "debug-" + UUID.randomUUID() + ".txt";
        out.put("attemptedKey", key);

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(spacesProperties.getBucket())
                    .key(key)
                    .contentType("text/plain")
                    .build();
            s3Client.putObject(req, software.amazon.awssdk.core.sync.RequestBody.fromString("debug"));
            out.put("status", "ok");
        } catch (S3Exception e) {
            out.put("status", "s3-exception");
            out.put("statusCode", e.statusCode());
            out.put("requestId", e.requestId());
            out.put("extendedRequestId", e.extendedRequestId());
            if (e.awsErrorDetails() != null) {
                out.put("errorCode", e.awsErrorDetails().errorCode());
                out.put("errorMessage", e.awsErrorDetails().errorMessage());
                out.put("serviceName", e.awsErrorDetails().serviceName());
                if (e.awsErrorDetails().sdkHttpResponse() != null) {
                    out.put("responseHeaders", e.awsErrorDetails().sdkHttpResponse().headers());
                }
                if (e.awsErrorDetails().rawResponse() != null) {
                    out.put("responseBody", e.awsErrorDetails().rawResponse().asUtf8String());
                }
            }
        } catch (Exception e) {
            out.put("status", "other-exception");
            out.put("type", e.getClass().getName());
            out.put("message", e.getMessage());
        }

        return ResponseEntity.ok(out);
    }
}
