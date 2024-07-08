package com.lbadvisors.pffc.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lbadvisors.pffc.configuration.AwsProperties;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class AwsService {

    @Autowired
    private Tika tika;

    @Autowired
    private AwsProperties awsProperties;

    @Autowired
    private S3Client s3Client;

    public void uploadFile(String s3FileKey, MultipartFile multipartFile) {

        // check if the file is empty
        if (multipartFile.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        // Check if the file is an image
        try {
            String contentType = tika.detect(multipartFile.getInputStream());
            if (!contentType.startsWith("image")) {
                throw new IllegalArgumentException("Only images are allowed");
            }

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(awsProperties.getBucketName() + "##")
                    .key(s3FileKey)
                    .build();

            InputStream inputStream = multipartFile.getInputStream();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(inputStream, inputStream.available()));

        } catch (AwsServiceException | SdkClientException | IOException e) {
            throw new RuntimeException("Error uploading the image");
        }

    };
}