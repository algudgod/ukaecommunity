package com.community.ukae.service.s3;

import com.community.ukae.entity.comment.Comment;
import com.community.ukae.entity.imageFile.ImageFile;
import com.community.ukae.entity.user.User;
import com.community.ukae.repository.comment.CommentRepository;
import com.community.ukae.repository.imageFile.ImageFileRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3Client;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ImageFileRepository imageFileRepository;

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

    // 프로필 단일 이미지 업로드
    public String uploadUserProfile(MultipartFile file, User user) throws IOException {
        String fileUrl = uploadFile(file); // 공통 파일 업로드 호출
        user.setProfileUrl(fileUrl);
        userRepository.save(user); // DB 업데이트
        logger.info("사용자 프로필 업데이트 성공: userId={}, profileUrl={}", user.getLoginId(), fileUrl);
        return fileUrl;
    }

    // 댓글 이미지 단일 업로드
    public String uploadCommentImage(MultipartFile file, int commentNo) throws IOException {

        // 댓글 조회
        Comment comment = commentRepository.findByCommentNo(commentNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다. commentNo=" + commentNo));

        String fileUrl = uploadFile(file); // 단일 파일 업로드 호출

        ImageFile imageFile = new ImageFile();
        imageFile.setComment(comment); // 댓글과 연결
        imageFile.setBoard(comment.getBoard()); // 댓글에 연결된 게시글과 연결
        imageFile.setImageUrl(fileUrl); // 반환 받은 S3 Url 저장
        imageFileRepository.save(imageFile); // DB에 저장
        logger.info("댓글 이미지 업로드 성공: commentNo={}, originalFilename={}", commentNo, fileUrl);

        return fileUrl;
    }

    // 다중 이미지 업로드
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {

        if (files == null || files.isEmpty()) {
            logger.info("업로드할 파일이 없습니다.");
            return Collections.emptyList();
        }
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

