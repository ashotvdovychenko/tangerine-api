package com.example.tangerine.api.service;

import org.springframework.core.io.Resource;

public interface StorageService {
  void uploadPicture(byte[] file, String objectKey, String bucket);

  Resource findByKey(String objectKey, String bucket);

  void deleteByKey(String objectKey, String bucket);
}
