package com.project.zighang.global.client.objectStorage.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.zighang.domain.post.repository.PostApplyEntityRepository;
import com.project.zighang.domain.post.repository.PostEntityRepository;
import com.project.zighang.domain.post.repository.ResumeFileEntityRepository;
import com.project.zighang.global.client.objectStorage.PresignedUrlReader;
import com.project.zighang.global.client.objectStorage.dto.PreSignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NcpPresignedUrlReader implements PresignedUrlReader {

    private final AmazonS3 ncpS3Client;

    private final ResumeFileEntityRepository resumeFileEntityRepository;
    private final PostEntityRepository postEntityRepository;
    private final PostApplyEntityRepository postApplyEntityRepository;

    @Value("${cloud.ncp.object-storage.bucket-name}")
    private String bucket;

    @Override
    public PreSignedUrlResponse getPreSignedUrl(String prefix, String originalFileName) {
        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);

        String key = String.format("%s/%s-%s", prefix, UUID.randomUUID(), encodedFileName);

        Date expiration = new Date(System.currentTimeMillis() + 60 * 1000);

        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        req.addRequestParameter(Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());

        URL presignedUrl = ncpS3Client.generatePresignedUrl(req);
        String objectUrl = ncpS3Client.getUrl(bucket, key).toString();

        return PreSignedUrlResponse.of(presignedUrl.toString(), objectUrl);
    }

    public boolean isFileExists(String objectKey) {
        try {
            log.info("파일 존재 확인 - bucket: {}, objectKey: {}", bucket, objectKey);

            ObjectMetadata metadata = ncpS3Client.getObjectMetadata(bucket, objectKey);
            log.info("파일 존재 확인 성공 - 파일 크기: {}", metadata.getContentLength());
            return true;

        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException - StatusCode: {}, ErrorCode: {}, Message: {}",
                    e.getStatusCode(), e.getErrorCode(), e.getMessage());

            if (e.getStatusCode() == 404) {
                log.warn("파일을 찾을 수 없습니다: {}", objectKey);
                return false;
            }
            throw new RuntimeException("파일 존재 확인 중 오류 발생: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 존재 확인 중 예상치 못한 오류 발생", e);
        }
    }

    public String extractObjectKeyFromUrl(String objectUrl) {
        try {
            URL url = new URL(objectUrl);
            String path = url.getPath().substring(1); // 앞의 '/' 제거

            // 버킷명 제거
            if (path.startsWith(bucket + "/")) {
                return path.substring(bucket.length() + 1);
            }
            return path;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 Object URL입니다", e);
        }
    }
}