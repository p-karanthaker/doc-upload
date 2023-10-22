package me.karanthaker.s3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class StorageService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private String bucket;

    @Transactional
    public void persist(UUID uuid, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .key(String.valueOf(uuid))
                .bucket(bucket)
                .metadata(Map.of("name", Objects.requireNonNull(file.getOriginalFilename())))
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public InputStream fetch(UUID uuid) {
        GetObjectRequest request = GetObjectRequest.builder()
                .key(String.valueOf(uuid))
                .bucket(bucket)
                .build();
        return s3Client.getObjectAsBytes(request).asInputStream();
    }
}
