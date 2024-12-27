package com.community.ukae.service.s3;

import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, User user) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        //String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. (최대 5MB)");
        }

        // S3 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String fileUrl = getFileUrl(fileName);

        // 2. DB 업데이트
        user.setProfileUrl(fileUrl);
        userRepository.save(user);

        return fileUrl; // 반환된 URL
    }

    // 이미지 URL 생성
    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucketName, fileName);
    }
}

