package com.campusconnect.campusconnect.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.enabled", havingValue = "true", matchIfMissing = false)
public class ObjectStorageService {
    private final S3Client s3;
    private final String bucket;
    private final String publicBaseUrl;

    public ObjectStorageService(
            S3Client s3,
            @Value("${storage.bucket}") String bucket,
            @Value("${storage.public-base-url}") String publicBaseUrl) {
        this.s3=s3; this.bucket=bucket; this.publicBaseUrl=publicBaseUrl.replaceAll("/$","");
    }

    public StoredObject upload(MultipartFile file) throws IOException {
        if(file==null || file.isEmpty()) throw new IllegalArgumentException("Choose a file");
        String original=file.getOriginalFilename()==null ? "upload" : file.getOriginalFilename();
        String safe=original.replaceAll("[^a-zA-Z0-9._-]","_");
        String key="campusconnect/"+UUID.randomUUID()+"-"+safe;
        String contentType=file.getContentType()==null ? "application/octet-stream" : file.getContentType();

        PutObjectRequest request=PutObjectRequest.builder()
                .bucket(bucket).key(key).contentType(contentType)
                .contentLength(file.getSize())
                .build();

        // Streams directly from the multipart request into S3/R2; no local upload file is created.
        try(var in=file.getInputStream()){
            s3.putObject(request, RequestBody.fromInputStream(in,file.getSize()));
        }
        return new StoredObject(publicBaseUrl+"/"+key, key, contentType);
    }

    public record StoredObject(String url,String key,String contentType) {}
}
