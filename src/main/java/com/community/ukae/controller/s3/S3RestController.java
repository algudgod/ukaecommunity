package com.community.ukae.controller.s3;

import com.community.ukae.entity.user.User;
import com.community.ukae.repository.user.UserRepository;
import com.community.ukae.service.s3.S3Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3RestController {

    private final S3Client s3Client; // S3Client로 변경
    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    // S3 버킷 존재 여부 확인
    @GetMapping("/test-s3")
    public void testS3Connection() {
        try {
            boolean exists = s3Client.listBuckets().buckets().stream()
                    .anyMatch(bucket -> bucket.name().equals(bucketName));
            if (exists) {
                System.out.println("S3 버킷이 존재합니다: " + bucketName);
            } else {
                System.out.println("S3 버킷이 존재하지 않습니다: " + bucketName);
            }
        } catch (Exception e) {
            System.err.println("S3 연결 오류: " + e.getMessage());
        }
    }

    // 회원 프로필 사진 업로드
    @PostMapping("/profileUpload")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // S3 업로드 및 URL 생성
            String fileUrl = s3Service.uploadFile(file, user);
            System.out.println("made fileUrl: " + fileUrl);

            return ResponseEntity.ok(fileUrl);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("업로드 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("uploadFiles")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> fileUrls = s3Service.uploadFiles(files);
            System.out.println("made fileUrl: " + fileUrls);

            // 성공 응답 반환
            return ResponseEntity.ok().body(fileUrls);
        } catch (IOException e) {
            // 예외 처리: 실패 시 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 오류가 발생했습니다.");
        }
    }
}