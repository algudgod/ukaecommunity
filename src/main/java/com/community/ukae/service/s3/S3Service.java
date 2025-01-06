package com.community.ukae.service.s3;

import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3Client;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String IMAGE_CONTENT_TYPE_PREFIX = "image/";

    // 단일 이미지 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        logger.info("파일 업로드 시작: originalFilename={}", file.getOriginalFilename());

        validateFile(file); // 파일 검증
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_" + file.getOriginalFilename();

        try {
            String fileUrl = uploadToS3(file, fileName); // S3 업로드
            logger.info("파일 업로드 성공: fileName={}", fileName);
            return fileUrl; // 업로드된 파일 URL 반환
        } catch (Exception e) {
            logger.error("파일 업로드 실패: originalFilename={}, 이유={}", file.getOriginalFilename(), e.getMessage(), e);
            throw e;
        }
    }

    public String uploadUserProfile(MultipartFile file, User user) throws IOException {
        String fileUrl = uploadFile(file); // 공통 파일 업로드 호출
        user.setProfileUrl(fileUrl);
        userRepository.save(user); // DB 업데이트
        logger.info("사용자 프로필 업데이트 성공: userId={}, profileUrl={}", user.getLoginId(), fileUrl);
        return fileUrl;
    }

    // 다중 이미지 업로드
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        logger.info("다중 파일 업로드 시작: 파일 수={}", files.size());
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            validateFile(file);
            String fileName = UUID.randomUUID().toString().substring(0, 8) + "_" + file.getOriginalFilename();

            try {
                String fileUrl = uploadToS3(file, fileName);
                logger.info("파일 업로드 성공: fileName={}, fileUrl={}", fileName, fileUrl);
                fileUrls.add(fileUrl);
            } catch (Exception e) {
                logger.error("파일 업로드 실패: originalFilename={}, 이유={}", file.getOriginalFilename(), e.getMessage(), e);
                throw e;
            }
        }

        logger.info("다중 파일 업로드 완료: 업로드된 파일 수={}", fileUrls.size());
        return fileUrls;
    }

    // 공통 S3 파일 업로드
    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
        logger.debug("S3 업로드 요청: fileName={}, bucketName={}", fileName, bucketName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            logger.debug("S3 업로드 성공: fileName={}", fileName);
        } catch (Exception e) {
            logger.error("S3 업로드 실패: fileName={}, 이유={}", fileName, e.getMessage(), e);
            throw e;
        }

        return getFileUrl(fileName);
    }

    // 업로드 파일 검증
    private void validateFile(MultipartFile file) {
        logger.debug("파일 검증 시작: originalFilename={}", file.getOriginalFilename());

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX)) {
            logger.warn("파일 검증 실패 - 지원되지 않는 파일 형식: originalFilename={}, contentType={}", file.getOriginalFilename(), contentType);
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("파일 검증 실패 - 파일 크기 초과: originalFilename={}, size={}bytes", file.getOriginalFilename(), file.getSize());
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. (최대 5MB)");
        }

        logger.debug("파일 검증 성공: originalFilename={}", file.getOriginalFilename());
    }

    // 이미지 URL 생성
    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucketName, fileName);
    }
}

