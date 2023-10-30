package com.example.tangerine.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AwsS3StorageService {

  private final S3Client client;

  public void uploadPicture(byte[] file, String objectKey, String bucket) {
    var request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(objectKey)
        .build();
    client.putObject(request, RequestBody.fromBytes(file));
  }

  public Resource findByKey(String objectKey, String bucket) {
    var request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(objectKey)
        .build();
    return new InputStreamResource(client.getObject(request));
  }

  public void deleteByKey(String objectKey, String bucket) {
    var request = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(objectKey)
        .build();
    client.deleteObject(request);
  }
}
