package com.company.ccm.document.infrastructure;

import java.io.InputStream;
import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@EnableConfigurationProperties(StorageProperties.class)
public class ObjectStorageService {

    private final StorageProperties properties;
    private volatile S3Client s3Client;

    public ObjectStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    private S3Client client() {
        if (s3Client == null) {
            synchronized (this) {
                if (s3Client == null) {
                    s3Client = S3Client.builder()
                            .endpointOverride(URI.create(properties.getEndpoint()))
                            .region(Region.of(properties.getRegion()))
                            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                    properties.getAccessKey(), properties.getSecretKey())))
                            .serviceConfiguration(
                                    S3Configuration.builder().pathStyleAccessEnabled(true).build())
                            .build();
                    ensureBucket(s3Client);
                }
            }
        }
        return s3Client;
    }

    private void ensureBucket(S3Client client) {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(properties.getBucket()).build());
        } catch (NoSuchBucketException e) {
            client.createBucket(CreateBucketRequest.builder().bucket(properties.getBucket()).build());
        }
    }

    public void upload(String key, InputStream inputStream, long size, String contentType) {
        client().putObject(
                PutObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(inputStream, size));
    }

    public InputStream download(String key) {
        return client()
                .getObject(GetObjectRequest.builder().bucket(properties.getBucket()).key(key).build());
    }
}
