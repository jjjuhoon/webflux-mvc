package com.example.helloworldmvc.apiPayload.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.helloworldmvc.config.AmazonConfig;
import com.example.helloworldmvc.domain.Uuid;
import com.example.helloworldmvc.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final S3Client s3Client;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        String contentType=file.getContentType();
        metadata.setContentLength(file.getSize());
        try {
            software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(amazonConfig.getBucket())
                    .key(keyName)
                    .contentType(contentType)
                    .contentDisposition("inline")
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
//        return String.format("https://%s.s3.amazonaws.com/%s", amazonConfig.getBucket(), keyName);

    }

    public String generateUserKeyName(Uuid uuid) {
        return amazonConfig.getUserPath() + '/' + uuid.getUuid();
    }

}